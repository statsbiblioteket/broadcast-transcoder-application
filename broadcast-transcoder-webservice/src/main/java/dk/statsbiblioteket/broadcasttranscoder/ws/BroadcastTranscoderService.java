package dk.statsbiblioteket.broadcasttranscoder.ws;

import dk.statsbiblioteket.broadcasttranscoder.btaws.BtaResponse;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 */
@Path("/bta")
public class BroadcastTranscoderService {

    public static Logger logger = LoggerFactory.getLogger(BroadcastTranscoderService.class);

    @Context
    ServletConfig config;
    @Context
    ServletContext context;

    @GET @Path("/btaDVDTranscode")
    @Produces(MediaType.APPLICATION_XML)
    public BtaResponse startDigitvTranscoding(
            @QueryParam("programpid") String programPid,
            @QueryParam("title") String title,
            @QueryParam("channel") String channel,
            @QueryParam("date") long startTime,
            @QueryParam("additional_start_offset") long additionalStartOffset,
            @QueryParam("additional_end_offset") long additionalEndOffset,
            @QueryParam("filename_prefix") String filenamePrefix,
            @DefaultValue("true") @QueryParam("burn_subtitles") String burnSubtitles)
            throws ProcessorException {
        final String programDescription = " " + title + " " + programPid + " " + channel + " " + startTime;
        logger.info("Received request for" + programDescription);
        checkContext();
        TranscodeRequest request = new TranscodeRequest();
        SingleTranscodingContext<BroadcastTranscodingRecord> transcodingContext = (SingleTranscodingContext<BroadcastTranscodingRecord>) context.getAttribute("transcodingContext");
        request.setObjectPid(programPid);
        request.setOutputBasename(title+"_"+startTime+"_"+additionalStartOffset+"_"+additionalEndOffset);
        BtaResponse response = new BtaResponse();
        response.setFilename(request.getOutputBasename());
        boolean isTranscoding = FileUtils.hasTemporarMediaOutputFile(request, transcodingContext);
        boolean isComplete = FileUtils.hasFinalMediaOutputFile(request, transcodingContext);
        if (isTranscoding) {
            response.setStatus("STARTED");
            response.setFilename(FileUtils.findTemporaryMediaOutputFile(request, transcodingContext).getAbsolutePath());
            logger.debug("Already started" + programDescription);
            return response;
        } else if (isComplete) {
            response.setStatus("DONE");
            response.setFilename(FileUtils.findFinalMediaOutputFile(request, transcodingContext).getAbsolutePath());
            logger.debug("Already complete" + programDescription);
            return response;
        } else {
            logger.debug("Starting new transcoding for" + programDescription);
            response.setStatus("STARTING");
            return response;
        }
    }

    /**
     *
     * @param programPid
     * @param title
     * @param channel
     * @param startTime
     * @param additionalStartOffset
     * @param additionalEndOffset
     * @param filenamePrefix
     * @param sendEmailParam Ignored. Exists for backwards compatibility.
     * @param alternative  Ignored. Exists for backwards compatibility.
     * @return
     */
    @GET @Path("/digitv_transcode")
    @Produces(MediaType.APPLICATION_XML)
    public BtaResponse startDigitvTranscoding(
            @QueryParam("programpid") String programPid,
            @QueryParam("title") String title,
            @QueryParam("channel") String channel,
            @QueryParam("date") long startTime,
            @QueryParam("additional_start_offset") long additionalStartOffset,
            @QueryParam("additional_end_offset") long additionalEndOffset,
            @QueryParam("filename_prefix") String filenamePrefix,
            @DefaultValue("false") @QueryParam("send_email") String sendEmailParam,
            @DefaultValue("false") @QueryParam("alternative") String alternative ) throws ProcessorException {
        return startDigitvTranscoding(programPid, title, channel, startTime,
                additionalStartOffset, additionalEndOffset, filenamePrefix, "true");
    }


    public void checkContext() throws ProcessorException {
        Object o = context.getAttribute("transcodingContext");
        if (! (o instanceof SingleTranscodingContext)) {
            throw new ProcessorException("Web context not initialised with SingleTranscodingContext");
        }
    }

}
