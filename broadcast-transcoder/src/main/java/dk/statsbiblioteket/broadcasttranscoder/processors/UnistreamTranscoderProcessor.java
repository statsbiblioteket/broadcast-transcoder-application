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
import java.util.stream.Collectors;

public class UnistreamTranscoderProcessor extends ProcessorChainElement {

    private static Logger log = LoggerFactory.getLogger(UnistreamTranscoderProcessor.class);

    public UnistreamTranscoderProcessor() {
    }

    public UnistreamTranscoderProcessor(ProcessorChainElement childElement) {
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
        } else if (request.getFileFormat().equals(FileFormatEnum.SINGLE_PROGRAM_AUDIO_TS)){
            line = "ffmpeg -ss $$START_OFFSET$$ -t $$LENGTH$$ "
                   + " $$INPUT_FILES$$ -ss 00:00:05 "
                   + " -acodec libmp3lame -ar 44100 -ac 2 "
                   + " -b:a $$AUDIO_BITRATE$$000 -y $$OUTPUT_FILE$$";
    
        }
        line = line.replace("$$AUDIO_BITRATE$$", context.getAudioBitrate()+"");
        line = line.replace("$$VIDEO_BITRATE$$", context.getVideoBitrate()+"");
        line = line.replace("$$FFMPEG_ASPECT_RATIO$$", getFfmpegAspectRatio(request, context));
        
        line = line.replace("$$OUTPUT_FILE$$", FileUtils.getTemporaryMediaOutputFile(request, context).getAbsolutePath());
    
        //Concat only works for transportStreams, but no mpeg file will ever have more than one clip, so this is not a problem
        String inputFiles = "-i \"concat:"
                            + request.getClips()
                                     .stream()
                                     .map(TranscodeRequest.FileClip::getFilepath)
                                     .collect(Collectors.joining("|"))
                            + "\"";
        line = line.replace("$$INPUT_FILES$$", inputFiles);
    
        line = handleOffsetAndEnd(request, context, line);
        return line;
    }
    
    private static String handleOffsetAndEnd(TranscodeRequest request, SingleTranscodingContext context, String line) {
        long programStartMillis = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart());
        long programEndMillis = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop());
        int startOffsetSeconds;
        int endOffsetSeconds;
        switch (request.getFileFormat()) {
            case MPEG_PS:
                if (request.isTvmeter()) {
                    startOffsetSeconds = context.getStartOffsetPSWithTVMeter();
                    endOffsetSeconds = context.getEndOffsetPSWithTVMeter();
                } else {
                    startOffsetSeconds = context.getStartOffsetPS();
                    endOffsetSeconds = context.getEndOffsetPS();
                }
                break;
            case AUDIO_WAV:
                startOffsetSeconds = context.getStartOffsetWAV();
                endOffsetSeconds = context.getEndOffsetWAV();
                break;
            default:
                if (request.isTvmeter()) {
                    startOffsetSeconds = context.getStartOffsetTSWithTVMeter();
                    endOffsetSeconds = context.getEndOffsetTSWithTVMeter();
                } else {
                    startOffsetSeconds = context.getStartOffsetTS();
                    endOffsetSeconds = context.getEndOffsetTS();
                }
                break;
        }
        startOffsetSeconds += request.getAdditionalStartOffset();
        endOffsetSeconds += request.getAdditionalEndOffset();
        programStartMillis += startOffsetSeconds*1000L;
        programEndMillis += endOffsetSeconds*1000L;
        
        Long firstFileStartTimeMillis = request.getClips().get(0).getFileStartTime();

        //We start 5 secs before, and then skip the first 5 secs of the transcoding. This ensures misaligned frames
        // do not destroy the first second of the transcoding
        long programStartSecondsInFirstFile = (programStartMillis - firstFileStartTimeMillis) / 1000 - 5;

        long programLengthSeconds = (programEndMillis - programStartMillis) / 1000;

        line = line.replace("$$START_OFFSET$$", programStartSecondsInFirstFile + "");
        line = line.replace("$$LENGTH$$", programLengthSeconds + "");
        return line;
    }
    


    protected static String getFfmpegAspectRatio(TranscodeRequest request, SingleTranscodingContext context) {
        if (request.getDisplayAspectRatio() != null) {
            Double aspectRatio = request.getDisplayAspectRatio();
            Long height = (long) context.getVideoHeight();
            if (aspectRatio != null) {
                long width = 2 * Math.round(aspectRatio * height / 2);
                //if (width%2 == 1) width += 1;
                return width + "x" + height;
            } else {
                return "320x240";
            }
        } else {
            return "";
        }
    }

}
