package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/28/13
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TranscoderApplication {

    private static Logger logger = LoggerFactory.getLogger(TranscoderApplication.class);

    protected static <T extends TranscodingRecord> void transcodingComplete(TranscodeRequest request, SingleTranscodingContext<T> context) {
        T record =  context.getTranscodingProcessInterface().read(context.getProgrampid());
        record.setTranscodingState(TranscodingStateEnum.COMPLETE);
        record.setFailureMessage("");
        record.setLastTranscodedTimestamp(context.getTranscodingTimestamp());
        context.getTranscodingProcessInterface().update(record);
    }

    protected static <T extends TranscodingRecord> void reject(TranscodeRequest request, SingleTranscodingContext<T> context) {
        logger.info("Transcoding rejected for " + context.getProgrampid() + ". Exiting.");
        context.getTranscodingProcessInterface().markAsRejected(context.getProgrampid(),"Message?");
    }

    protected static <T extends TranscodingRecord> void alreadyTranscoded(TranscodeRequest request, SingleTranscodingContext<T> context) {

        logger.info("No transcoding required for " + context.getProgrampid() + ". Exiting.");
        context.getTranscodingProcessInterface().markAsAlreadyTranscoded(context.getProgrampid());
    }

    protected static <T extends TranscodingRecord> void transcodingFailed(TranscodeRequest request, SingleTranscodingContext<T> context, Exception e) {
        T record =  context.getTranscodingProcessInterface().read(context.getProgrampid());
        record.setTranscodingState(TranscodingStateEnum.FAILED);
        record.setLastTranscodedTimestamp(context.getTranscodingTimestamp());
        record.setFailureMessage(e.getMessage());
        context.getTranscodingProcessInterface().update(record);
    }


}
