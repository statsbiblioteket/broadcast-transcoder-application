package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 11/28/12
 * Time: 11:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class UnistreamAudioTranscoderProcessor extends ProcessorChainElement {

    private static Logger log = LoggerFactory.getLogger(UnistreamAudioTranscoderProcessor.class);




    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        String command = getFfmpegCommandLine(request, context);
                File outputDir = FileUtils.getTemporaryMediaOutputDir(request, context);
                outputDir.mkdirs();
                File outputFile = FileUtils.getTemporaryMediaOutputFile(request, context);
                try {
                    long timeout = MetadataUtils.getTimeout(request, context);
                    log.debug("Setting transcoding timeout for '" + request.getObjectPid() + "' to " + timeout + "ms");
                    request.setTranscoderCommand(command);
                    ExternalJobRunner.runClipperCommand(timeout, command);
                } catch (ExternalProcessTimedOutException e) {
                    FileUtils.deleteAndLogFailedFile(outputFile, e);
                    throw new ProcessorException("External process timed out for " + request.getObjectPid(),e);
                }
    }

     public static String getFfmpegCommandLine(TranscodeRequest request, SingleTranscodingContext context) {
           File outputFile = FileUtils.getTemporaryMediaOutputFile(request, context);
           String line = "ffmpeg -ss $$START_OFFSET$$ -t $$LENGTH$$ $$INPUT_FILES$$ -acodec libmp3lame -ar 44100 -ac 2 "
                   + " -b:a " + context.getAudioBitrate() + "000 -y "
                   + outputFile.getAbsolutePath();
    
         String inputFiles = getInputFiles(request);
         line = line.replace("$$INPUT_FILES$$",inputFiles);
    
         line = handleOffsetAndEnd(request, context, line);
         return line;
     }
    
    private static String handleOffsetAndEnd(TranscodeRequest request, SingleTranscodingContext context, String line) {
        long programStart = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart());
        long programEnd = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop());
        int startOffset;
        int endOffset;
        switch (request.getFileFormat()) {
            case MPEG_PS:
                if (request.isTvmeter()) {
                    startOffset = context.getStartOffsetPSWithTVMeter();
                    endOffset = context.getEndOffsetPSWithTVMeter();
                } else {
                    startOffset = context.getStartOffsetPS();
                    endOffset = context.getEndOffsetPS();
                }
                break;
            case AUDIO_WAV:
                startOffset = context.getStartOffsetWAV();
                endOffset = context.getEndOffsetWAV();
                break;
            default:
                if (request.isTvmeter()) {
                    startOffset = context.getStartOffsetTSWithTVMeter();
                    endOffset = context.getEndOffsetTSWithTVMeter();
                } else {
                    startOffset = context.getStartOffsetTS();
                    endOffset = context.getEndOffsetTS();
                }
                break;
        }
        startOffset += request.getAdditionalStartOffset();
        endOffset += request.getAdditionalEndOffset();
        programStart += startOffset*1000L;
        programEnd += endOffset*1000L;
        
        line = line.replace("$$START_OFFSET$$",(programStart-request.getClips().get(0).getFileStartTime())/1000+"");
        line = line.replace("$$LENGTH$$",(programEnd-programStart)/1000+"");
        return line;
    }
    
    private static String getInputFiles(TranscodeRequest request) {
        StringBuilder inputFiles = new StringBuilder();
        inputFiles.append("-i \"concat:");
        for (TranscodeRequest.FileClip fileClip : request.getClips()) {
            inputFiles.append(fileClip.getFilepath()).append("|");
        }
        inputFiles.append("\"");
        return inputFiles.toString();
    }
    
}
