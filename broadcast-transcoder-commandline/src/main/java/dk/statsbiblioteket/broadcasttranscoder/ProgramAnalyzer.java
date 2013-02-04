package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.ProgramAnalyzerContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.parsers.ProgramAnalyzerOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.cli.parsers.SingleTranscodingOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.*;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class ProgramAnalyzer extends TranscoderApplication{

    private static Logger logger = LoggerFactory.getLogger(ProgramAnalyzer.class);

    public static void main(String[] args) throws Exception {
        logger.debug("Entered main method.");
        ProgramAnalyzerContext<BroadcastTranscodingRecord> context = null;

        File lockFile = null;
        try {
            context = new ProgramAnalyzerOptionsParser<BroadcastTranscodingRecord>().parseOptions(args);
            HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
            context.setTranscodingProcessInterface(new BroadcastTranscodingRecordDAO(util));
        } catch (Exception e) {
            logger.error("Error in initial environment", e);
            System.exit(5);
        }
        for (String pid : context.getPidList()) {
            TranscodeRequest request;
            request = new TranscodeRequest();
            request.setObjectPid(pid);

            lockFile = FileUtils.getLockFile(request, context);
            handleLock(lockFile);

            try {
                runChain(request, context);
                if (request.isRejected()) {
                    System.exit(111);
                }

            } catch (Exception e) {
                transcodingFailed(request,context,e);
                //Final fault barrier is necessary for logging
                logger.error("Processing failed for " + request.getObjectPid(), e);
                throw(e);
            } finally {
                logger.debug("Deleting lockfile " + lockFile.getAbsolutePath());
                boolean deleted = lockFile.delete();
                if (!deleted) {
                    logger.error("Could not delete lockfile: " + lockFile.getAbsolutePath());
                    System.exit(0);
                }
            }
            logger.info("All processing finished for " + request.getObjectPid());
            System.out.println(request.getObjectPid());
        }

    }

    private static void handleLock(File lockFile) {
        if (lockFile.exists()) {
            logger.warn("Lockfile " + lockFile.getAbsolutePath() + " already exists. Exiting.");
            System.exit(0);
        }
        try {
            logger.debug("Creating lockfile " + lockFile.getAbsolutePath());
            boolean created = lockFile.createNewFile();
            if (!created) {
                logger.error("Could not create lockfile: " + lockFile.getAbsolutePath() + ". Exiting.");
                System.exit(3);
            }
        } catch (IOException e) {
            logger.error("Could not create lockfile: " + lockFile.getAbsolutePath() + ". Exiting.");
            System.exit(3);
        }
    }


    public static ProcessorChainElement getChain(){

        ProcessorChainElement programFetcher = new ProgramMetadataFetcherProcessor();
        ProcessorChainElement pbcorer = new PbcoreMetadataExtractorProcessor();
        ProcessorChainElement filedataFetcher = new FileMetadataFetcherProcessor();
        ProcessorChainElement sanitiser = new SanitiseBroadcastMetadataProcessor();
        ProcessorChainElement sorter = new BroadcastMetadataSorterProcessor();
        ProcessorChainElement fileFinderFetcher = new FilefinderFetcherFakerProcessor();
        ProcessorChainElement identifier = new FilePropertiesIdentifierProcessor();
        ProcessorChainElement clipper = new ClipFinderProcessor();

        ProcessorChainElement coverage = new CoverageAnalyserProcessor();
        ProcessorChainElement updater = new ProgramStructureUpdaterProcessor();
        ProcessorChainElement firstChain = ProcessorChainElement.makeChain(
                programFetcher,
                pbcorer,
                filedataFetcher,
                sanitiser,
                sorter,
                fileFinderFetcher,
                identifier,
                clipper,
                coverage,
                updater);
        return firstChain;

    }

    public static <T extends TranscodingRecord> void runChain(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {

        request.setGoForTranscoding(true);

        ProcessorChainElement firstChain = getChain();
        firstChain.processIteratively(request, context);
        if (!request.isGoForTranscoding()) {
            alreadyTranscoded(request, context);
            return;
        }
        if (request.isRejected()) {
            reject(request, context);
            return;
        }
    }


}
