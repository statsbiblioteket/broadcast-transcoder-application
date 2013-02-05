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
import org.hibernate.exception.LockAcquisitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * This program creates the PROGRAM_STRUCTURE datastream in the selected program objects. Used
 * to amend the old transcoded programs that were not analyzed.
 */
public class ProgramAnalyzer{

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
                request.setGoForTranscoding(true);
                ProcessorChainElement firstChain = getChain();
                firstChain.processIteratively(request, context);
                if (request.isRejected()) {
                    logger.error("Processing failed for " + request.getObjectPid());
                    System.err.println(request.getObjectPid());
                    continue;
                }


            } catch (Exception e) {
                logger.error("Processing failed for " + request.getObjectPid(), e);
                System.err.println(request.getObjectPid());
                continue;
            } finally {
                logger.debug("Deleting lockfile " + lockFile.getAbsolutePath());
                boolean deleted = lockFile.delete();
                if (!deleted) {
                    logger.error("Could not delete lockfile: " + lockFile.getAbsolutePath() + " for "+request.getObjectPid());
                    System.err.println(request.getObjectPid());
                }
            }
            logger.info("All processing finished for " + request.getObjectPid());
            System.out.println(request.getObjectPid());
        }

    }

    private static void handleLock(File lockFile) throws LockException {
        if (lockFile.exists()) {
            logger.warn("Lockfile " + lockFile.getAbsolutePath() + " already exists.");
            throw new LockException("Lockfile " + lockFile.getAbsolutePath() + " already exists.");
        }
        try {
            logger.debug("Creating lockfile " + lockFile.getAbsolutePath());
            boolean created = lockFile.createNewFile();
            if (!created) {
                logger.error("Could not create lockfile: " + lockFile.getAbsolutePath() + ".");
                throw new LockException("Could not create lockfile: " + lockFile.getAbsolutePath() + ".");
            }
        } catch (IOException e) {
            logger.error("Could not create lockfile: " + lockFile.getAbsolutePath() + ". Exiting.");
            throw new LockException("Could not create lockfile: " + lockFile.getAbsolutePath() + ".");
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



}
