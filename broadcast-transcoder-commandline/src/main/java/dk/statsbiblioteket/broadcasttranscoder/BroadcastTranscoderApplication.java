package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.fetcher.DomsTranscodingStructureFetcher;
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

    public static void main(String[] args) throws OptionParseException, ProcessorException {
        logger.debug("Entered main method.");
        Context context = new OptionsParser().parseOptions(args);
        HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
        context.setTimestampPersister(new BroadcastTranscodingRecordDAO(util));
        TranscodeRequest request = new TranscodeRequest();
        File lockFile = FileUtils.getLockFile(request, context);
        if (lockFile.exists()) {
            logger.warn("Lockfile " + lockFile.getAbsolutePath() + " already exists. Exiting.");
            System.exit(2);
        }
        try {
            boolean created = lockFile.createNewFile();
            if (!created) {
                logger.warn("Could not create lockfile: " + lockFile.getAbsolutePath() + ". Exiting.");
                System.exit(3);
            }
        } catch (IOException e) {
            logger.warn("Could not create lockfile: " + lockFile.getAbsolutePath() + ". Exiting.");
            System.exit(3);
        }
        try {
            ProcessorChainElement structureFetcher = new DomsTranscodingStructureFetcher();
            ProcessorChainElement programFetcher = new ProgramMetadataFetcherProcessor();
            ProcessorChainElement filedataFetcher    = new FileMetadataFetcherProcessor();
            ProcessorChainElement sorter = new BroadcastMetadataSorterProcessor();
            ProcessorChainElement fileFinderFetcher = new FilefinderFetcherProcessor();
            ProcessorChainElement identifier = new FilePropertiesIdentifierProcessor();
            ProcessorChainElement clipper = new ClipFinderProcessor();
            ProcessorChainElement coverage = new CoverageAnalyserProcessor();
            ProcessorChainElement updater = new ProgramStructureUpdaterProcessor();
            ProcessorChainElement fixer = new StructureFixerProcessor();
            ProcessorChainElement concatenator = new ClipConcatenatorProcessor();
            ProcessorChainElement firstChain = ProcessorChainElement.makeChain(
                    structureFetcher,
                    programFetcher,
                    filedataFetcher,
                    sorter,
                    fileFinderFetcher,
                    identifier,
                    clipper,
                    coverage,
                    updater,
                    fixer,
                    concatenator);
            firstChain.processIteratively(request, context);
            if (request.isGoForTranscoding()) {
                ProcessorChainElement secondChain = null;
                ProcessorChainElement pider = new PidAndAsepctRatioExtractorProcessor();
                ProcessorChainElement waver = new WavTranscoderProcessor();
                ProcessorChainElement multistreamer = new MultistreamVideoTranscoderProcessor();
                ProcessorChainElement unistreamvideoer = new UnistreamVideoTranscoderProcessor();
                ProcessorChainElement unistreamaudioer = new UnistreamAudioTranscoderProcessor();
                ProcessorChainElement previewer = new PreviewClipperProcessor();
                ProcessorChainElement snapshotter = new SnapshotExtractorProcessor();
                switch (request.getFileFormat()) {
                    case MULTI_PROGRAM_MUX:
                        secondChain = ProcessorChainElement.makeChain(pider, multistreamer, previewer, snapshotter);
                        break;
                    case SINGLE_PROGRAM_VIDEO_TS:
                        secondChain = ProcessorChainElement.makeChain(pider, unistreamvideoer, previewer, snapshotter);
                        break;
                    case SINGLE_PROGRAM_AUDIO_TS:
                        secondChain = ProcessorChainElement.makeChain(pider, unistreamaudioer, previewer);
                        break;
                    case MPEG_PS:
                        secondChain = ProcessorChainElement.makeChain(pider, unistreamvideoer, previewer, snapshotter);
                        break;
                    case AUDIO_WAV:
                        secondChain = ProcessorChainElement.makeChain(waver, previewer);
                        break;
                }
                secondChain.processIteratively(request, context);
                context.getTimestampPersister().setTimestamp(context.getProgrampid(), context.getTranscodingTimestamp());
            } else {
                logger.info("No transcoding required for " + context.getProgrampid() + ". Exiting.");
            }
        } finally {
            boolean deleted = lockFile.delete();
            if (!deleted) {
                logger.error("Could not delete lockfile: " + lockFile.getAbsolutePath());
                System.exit(4);
            }
        }
    }


}
