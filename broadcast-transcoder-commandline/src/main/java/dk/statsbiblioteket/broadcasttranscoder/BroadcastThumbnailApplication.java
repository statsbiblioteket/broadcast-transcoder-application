package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.UsageException;
import dk.statsbiblioteket.broadcasttranscoder.cli.parsers.SingleTranscodingOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.*;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An easy-to-use application which takes the uuid of an already-transcoded TV program, deletes any existing thumbnails,
 * and generates a new set of thumbnails.
 */
public class BroadcastThumbnailApplication extends TranscoderApplication {

    private static Logger logger = LoggerFactory.getLogger(BroadcastThumbnailApplication.class);


    public static void main(String[] args) throws OptionParseException, ProcessorException {
        logger.debug("Entered main method of " + BroadcastThumbnailApplication.class.getSimpleName());
        SingleTranscodingContext<BroadcastTranscodingRecord> context = null;
        TranscodeRequest request = null;
        try {
            try {
                context = new SingleTranscodingOptionsParser<BroadcastTranscodingRecord>().parseOptions(args);
            } catch (UsageException e) {
                return;
            }
            request = new TranscodeRequest();
            request.setObjectPid(context.getProgrampid());
        } catch (Exception e) {
            logger.error("Error in initial environment", e);
            throw new OptionParseException("Failed to parse options",e);
        }
        if (!FileUtils.hasFinalMediaOutputFile(request, context)) {
            logger.warn("Cannot generate snapshots as there is no transcoded file for " + request.getObjectPid());
            System.exit(2);
        }
        request.setFileFormat(FileFormatEnum.SINGLE_PROGRAM_VIDEO_TS);
        ProcessorChainElement deleter = new OldThumbnailDeleter();
        ProcessorChainElement analyser = new OutputFileFfprobeAnalyser();
        ProcessorChainElement extractor = new SnapshotExtractorProcessor();
        ProcessorChainElement chain = ProcessorChainElement.makeChain(deleter, analyser, extractor);
        String origThreadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(request.getObjectPid());
            chain.processIteratively(request, context);
            logger.info("Completed processing of {}.", request.getObjectPid());
        } finally {
            Thread.currentThread().setName(origThreadName);
        }
    }
}
