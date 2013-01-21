package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.processors.DomsAndOverwriteExaminerProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.*;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class BroadcastTranscoderApplication {

    private static Logger logger = LoggerFactory.getLogger(BroadcastTranscoderApplication.class);

    public static void main(String[] args) throws Exception {
        logger.debug("Entered main method.");
        Context context = null;
        TranscodeRequest request = null;
        File lockFile = null;
        try {
            context = new OptionsParser().parseOptions(args);
            HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
            context.setTimestampPersister(new BroadcastTranscodingRecordDAO(util));
            request = new TranscodeRequest();
            lockFile = FileUtils.getLockFile(request, context);
        } catch (Exception e) {
            logger.error("Error in initial environment", e);
            System.exit(5);
        }
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
        try {
            runChain(request, context);
            if (request.isRejected()) {
                System.exit(111);
            }
        } catch (Exception e) {
            //Final fault barrier is necessary for logging
            logger.error("Processing failed for " + context.getProgrampid(), e);
            throw(e);
        } finally {
            logger.debug("Deleting lockfile " + lockFile.getAbsolutePath());
            boolean deleted = lockFile.delete();
            if (!deleted) {
                logger.error("Could not delete lockfile: " + lockFile.getAbsolutePath());
                System.exit(0);
            }
        }
        logger.info("All processing finished for " + context.getProgrampid());
    }


    public static void runChain(TranscodeRequest request, Context context) throws ProcessorException {

            request.setGoForTranscoding(true);

            ProcessorChainElement structureFetcher = new DomsAndOverwriteExaminerProcessor();
            ProcessorChainElement preChain = ProcessorChainElement.makeChain(
                    structureFetcher);
            preChain.processIteratively(request, context);

            if (!request.isGoForTranscoding()) {
                context.getTimestampPersister().setTimestamp(context.getProgrampid(), context.getTranscodingTimestamp());
                logger.info("No transcoding required for " + context.getProgrampid() + ". Exiting.");
                return;
            }

            /*First one getting stuff for the persistence layer*/
            ProcessorChainElement programFetcher = new ProgramMetadataFetcherProcessor();

            /*Next one getting for the persistence layer*/
            ProcessorChainElement pbcorer = new PbcoreMetadataExtractorProcessor();


            ProcessorChainElement filedataFetcher    = new FileMetadataFetcherProcessor();
            ProcessorChainElement sanitiser = new SanitiseBroadcastMetadataProcessor();
            ProcessorChainElement sorter = new BroadcastMetadataSorterProcessor();
            ProcessorChainElement fileFinderFetcher = new FilefinderFetcherProcessor();
            ProcessorChainElement identifier = new FilePropertiesIdentifierProcessor();

            /*Find the offsets*/
            ProcessorChainElement clipper = new ClipFinderProcessor();

            ProcessorChainElement coverage = new CoverageAnalyserProcessor();
            ProcessorChainElement updater = new ProgramStructureUpdaterProcessor();
            ProcessorChainElement fixer = new StructureFixerProcessor();
            ProcessorChainElement concatenator = new ClipConcatenatorProcessor();
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
                    updater,
                    fixer,
                    concatenator);
            firstChain.processIteratively(request, context);
            if (!request.isGoForTranscoding()) {
                logger.info("No transcoding required for " + context.getProgrampid() + ". Exiting.");
                return;
            }
            if (request.isRejected()) {
                logger.info("Transcoding rejected for " + context.getProgrampid() + ". Exiting.");
                return;
            }
            ProcessorChainElement secondChain;
            ProcessorChainElement pider = new PidAndAsepctRatioExtractorProcessor();
            ProcessorChainElement waver = new WavTranscoderProcessor();
            ProcessorChainElement multistreamer = new MultistreamVideoTranscoderProcessor();
            ProcessorChainElement unistreamvideoer = new UnistreamVideoTranscoderProcessor();
            ProcessorChainElement unistreamaudioer = new UnistreamAudioTranscoderProcessor();
            ProcessorChainElement renamer = new FinalMediaFileRenamerProcessor();
            ProcessorChainElement previewer = new PreviewClipperProcessor();
            ProcessorChainElement snapshotter = new SnapshotExtractorProcessor();
            ProcessorChainElement zeroChecker = new ZeroLengthCheckerProcessor();
            ProcessorChainElement persistenceEnricher = new BroadcastTranscodingRecordEnricherProcessor();

            switch (request.getFileFormat()) {
                case MULTI_PROGRAM_MUX:
                    secondChain = ProcessorChainElement.makeChain(pider,
                            multistreamer,
                            renamer,
                            zeroChecker,
                            previewer,
                            snapshotter);
                    break;
                case SINGLE_PROGRAM_VIDEO_TS:
                    secondChain = ProcessorChainElement.makeChain(pider,
                            unistreamvideoer,
                            renamer,
                            zeroChecker,
                            previewer,
                            snapshotter);
                    break;
                case SINGLE_PROGRAM_AUDIO_TS:
                    secondChain = ProcessorChainElement.makeChain(pider,
                            unistreamaudioer,
                            renamer,
                            zeroChecker,
                            previewer);
                    break;
                case MPEG_PS:
                    secondChain = ProcessorChainElement.makeChain(pider,
                            unistreamvideoer,
                            renamer,
                            zeroChecker,
                            previewer,
                            snapshotter);
                    break;
                case AUDIO_WAV:
                    secondChain = ProcessorChainElement.makeChain(waver,
                            renamer,
                            zeroChecker,
                            previewer);
                    break;
                default:
                    return;
            }
        secondChain.processIteratively(request, context);
        context.getTimestampPersister().setTimestamp(context.getProgrampid(), context.getTranscodingTimestamp());
        ProcessorChainElement thirdChain = ProcessorChainElement.makeChain(persistenceEnricher);
            try {
                thirdChain.processIteratively(request, context);
            } catch (ProcessorException e) {
                //This is only a warning. Enrichment is only a nice-to-have.
                logger.warn("Persistence Enrichment failed for " + context.getProgrampid(), e);
            }
    }


}
