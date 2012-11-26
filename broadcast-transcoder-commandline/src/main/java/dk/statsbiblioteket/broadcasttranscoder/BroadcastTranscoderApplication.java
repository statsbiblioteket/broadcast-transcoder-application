package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.fetcher.DomsTranscodingStructureFetcher;
import dk.statsbiblioteket.broadcasttranscoder.processors.*;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
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
            ProcessorChainElement firstChain = makeChain(
                    structureFetcher,
                    programFetcher,
                    filedataFetcher,
                    sorter,
                    fileFinderFetcher,
                    identifier,
                    clipper,
                    coverage,
                    updater,
                    fixer);
            firstChain.processIteratively(request, context);
            if (request.isGoForTranscoding()) {
                ProcessorChainElement secondChain = null;
                ProcessorChainElement pider = new PidExtractorProcessor();
                ProcessorChainElement waver = new WavTranscoderProcessor();
                ProcessorChainElement tser = new MediestreamTransportStreamTranscoderProcessor();
                ProcessorChainElement pser = new ProgramStreamTranscoderProcessor();
                ProcessorChainElement previewer = new PreviewClipperProcessor();
                ProcessorChainElement snapshotter = new SnapshotExtractorProcessor();
                switch (request.getFileFormat()) {
                    case MULTI_PROGRAM_MUX:
                        secondChain = makeChain(pider, tser, previewer, snapshotter);
                        break;
                    case SINGLE_PROGRAM_VIDEO_TS:
                        secondChain = makeChain(pider, tser, previewer, snapshotter);
                        break;
                    case SINGLE_PROGRAM_AUDIO_TS:
                        secondChain = makeChain(pider, tser, previewer);
                        break;
                    case MPEG_PS:
                        secondChain = makeChain(pider, pser, previewer, snapshotter);
                        break;
                    case AUDIO_WAV:
                        secondChain = makeChain(waver, previewer);
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


    private static ProcessorChainElement makeChain(ProcessorChainElement... elements) {

        ProcessorChainElement previous = null;
        for (ProcessorChainElement element : elements) {
            if (previous != null) {
                previous.setChildElement(element);
            }
            previous = element;
        }
        return elements[0];

    }

}
