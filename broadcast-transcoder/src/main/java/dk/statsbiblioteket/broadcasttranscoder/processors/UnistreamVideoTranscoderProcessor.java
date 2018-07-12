/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class UnistreamVideoTranscoderProcessor extends ProcessorChainElement {

    private static Logger log = LoggerFactory.getLogger(UnistreamVideoTranscoderProcessor.class);

    public UnistreamVideoTranscoderProcessor() {
    }

    public UnistreamVideoTranscoderProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        mpegClip(request, context);
    }

    private void mpegClip(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        String command = getFfmpegCommandLine(request, context);
        File outputDir = FileUtils.getTemporaryMediaOutputDir(request, context);
        outputDir.mkdirs();
        File outputFile = FileUtils.getTemporaryMediaOutputFile(request, context);
        try {
            long timeout;
            if (request.getTimeoutMilliseconds() == 0l) {
                timeout = MetadataUtils.getTimeout(request, context);
            } else if (request.getFfprobeDurationSeconds() != null) {
                timeout = (long) (Math.round(request.getFfprobeDurationSeconds()*1000L
                        - request.getStartOffsetUsed()*1000L
                        + request.getEndOffsetUsed()*1000L
                )/context.getTranscodingTimeoutDivisor());
            } else {
                timeout = request.getTimeoutMilliseconds();
            }
            log.debug("Setting transcoding timeout for '" + request.getObjectPid() + "' to " + timeout + "ms");
            request.setTranscoderCommand(command);
            ExternalJobRunner.runClipperCommand(timeout, command);
        } catch (ExternalProcessTimedOutException e) {
            FileUtils.deleteAndLogFailedFile(outputFile, e);
            throw new ProcessorException("External process timed out for " + request.getObjectPid(),e);
        }
    }

    public static String getFfmpegCommandLine(TranscodeRequest request, SingleTranscodingContext context) {
        String line = context.getFfmpegTranscodingString();
        if (request.getFileFormat().equals(FileFormatEnum.SINGLE_PROGRAM_VIDEO_TS) && request.getDvbsubPid() != null){
            log.warn("Transcoding with subtitles, so exiting :) {},{}",request,context);
            //We have a transport stream with a subtitle track, so use it
            line = context.getFfmpegTranscodingWithSubtitlesString();
            line = line.replace("$$DVBSUB_STREAM$$", request.getDvbsubPid());
        } else if (request.getFileFormat().equals(FileFormatEnum.MPEG_PS) && context.getVideoOutputSuffix().equals("mpeg")) {
            //From mpeg to mpeg, no need to transcode, just do remux
            line = context.getVlcRemuxingString();
        }
        line = line.replace("$$AUDIO_BITRATE$$", context.getAudioBitrate()+"");
        line = line.replace("$$VIDEO_BITRATE$$", context.getVideoBitrate()+"");
        line = line.replace("$$FFMPEG_ASPECT_RATIO$$", getFfmpegAspectRatio(request, context));
        line = line.replace("$$OUTPUT_FILE$$", FileUtils.getTemporaryMediaOutputFile(request, context).getAbsolutePath());
        
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


    protected static String getFfmpegAspectRatio(TranscodeRequest request, SingleTranscodingContext context) {
        Double aspectRatio = request.getDisplayAspectRatio();
        String ffmpegResolution;
        Long height = context.getVideoHeight()*1L;
        if (aspectRatio != null) {
            long width = 2*Math.round(aspectRatio*height/2);
            //if (width%2 == 1) width += 1;
            ffmpegResolution =  width + "x" + height;
        } else {
            ffmpegResolution = " 320x240";
        }
        return ffmpegResolution;
    }

}
