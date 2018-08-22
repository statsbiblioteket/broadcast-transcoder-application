/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
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
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        mpegClip(request, context);
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
                   + " $$INPUT_FILES$$ -ss $$SKIP_SECONDS$$ "
                   + " -acodec libmp3lame -ar 44100 -ac 2 "
                   + " -b:a $$AUDIO_BITRATE$$000 -y $$OUTPUT_FILE$$";
    
        }
        line = line.replace("$$AUDIO_STREAM$$", request.getAudioStereoIndex());
        line = line.replace("$$AUDIO_BITRATE$$", context.getAudioBitrate()+"");
        line = line.replace("$$VIDEO_BITRATE$$", context.getVideoBitrate()+"");
        line = line.replace("$$FFMPEG_ASPECT_RATIO$$", getFfmpegAspectRatio(request, context));
        
        line = line.replace("$$OUTPUT_FILE$$", FileUtils.getTemporaryMediaOutputFile(request, context).getAbsolutePath());
    
        
        
        
        long programStartMillis = getProgramStartMillis(request, context);
    
        Long firstFileStartTimeMillis = request.getClips().get(0).getFileStartTime();
    
        //We start 5 secs before, and then skip the first 5 secs of the transcoding. This ensures misaligned frames
        // do not destroy the first second of the transcoding

        //We take it as max(,0), so that if the the program starts before the file, we do not get negative values
        long offsetIntoFirstFileSeconds = Math.max((programStartMillis - firstFileStartTimeMillis) / 1000,0);

        //If the program starts less than 5 seconds into the file, only skip to the start of the file, and no longer
        long skipSeconds=Math.min(offsetIntoFirstFileSeconds,5);
        
        line = line.replace("$$SKIP_SECONDS$$",skipSeconds+"");
        
        long programStartSecondsInFirstFile = offsetIntoFirstFileSeconds - skipSeconds;
    
    
        long programEndMillis = getProgramEndMillis(request, context);
    
    
        long programLengthSeconds = (programEndMillis - programStartMillis) / 1000;
    
        line = line.replace("$$LENGTH$$", programLengthSeconds + "");
    
        
        
    
        if (!request.getFileFormat().equals(FileFormatEnum.MPEG_PS)) {
            //Concat only works for transportStreams, but no mpeg file will ever have more than one clip, so this is not a problem
            String inputFiles = "-i \"concat:"
                                + request.getClips()
                                         .stream()
                                         .map(TranscodeRequest.FileClip::getFilepath)
                                         .collect(Collectors.joining("|"))
                                + "\"";
            line = line.replace("$$INPUT_FILES$$", inputFiles);
        } else {
            long offsetInFirstFile = programStartSecondsInFirstFile;
            String inputFiles = "-f concat -safe 0 -i <(echo -e \""
                                + request.getClips()
                                         .stream()
                                         .findFirst()
                                         .map(fileClip -> "file '" + fileClip.getFilepath() + "' \\n"
                                                          +"inpoint "+offsetInFirstFile+" \\n")
                                         .orElse("")
                                + request.getClips()
                                         .stream()
                                         .skip(1)
                                         .map(fileClip -> "file '" + fileClip.getFilepath() + "' ")
                                         .collect(Collectors.joining("\\n"))
                                + "\")";
            line = line.replace("$$INPUT_FILES$$", inputFiles);
            programStartSecondsInFirstFile = 0;
        }
    
        line = line.replace("$$START_OFFSET$$", programStartSecondsInFirstFile + "");
    
    
        return line;
    }
    
    private static long getProgramMillis(TranscodeRequest request,
                                         XMLGregorianCalendar timeStop,
                                         int endOffsetPSWithTVMeter,
                                         int endOffsetPS,
                                         int endOffsetWAV,
                                         int endOffsetTSWithTVMeter, int endOffsetTS, long additionalEndOffset) {
        long programEndMillis = CalendarUtils.getTimestamp(timeStop);
        
        int endOffsetSeconds;
        switch (request.getFileFormat()) {
            case MPEG_PS:
                if (request.isTvmeter()) {
                    endOffsetSeconds = endOffsetPSWithTVMeter;
                } else {
                    endOffsetSeconds = endOffsetPS;
                }
                break;
            case AUDIO_WAV:
                endOffsetSeconds = endOffsetWAV;
                break;
            default:
                if (request.isTvmeter()) {
                    endOffsetSeconds = endOffsetTSWithTVMeter;
                } else {
                    endOffsetSeconds = endOffsetTS;
                }
                break;
        }
        endOffsetSeconds += additionalEndOffset;
        programEndMillis += endOffsetSeconds * 1000L;
        return programEndMillis;
    }
    
    private static long getProgramStartMillis(TranscodeRequest request, SingleTranscodingContext context) {
        long programStartMillis = getProgramMillis(request,
                                                   request.getProgramBroadcast().getTimeStart(),
                                                   context.getStartOffsetPSWithTVMeter(),
                                                   context.getStartOffsetPS(),
                                                   context.getStartOffsetWAV(),
                                                   context.getStartOffsetTSWithTVMeter(),
                                                   context.getStartOffsetTS(),
                                                   request.getAdditionalStartOffset());
        return programStartMillis;
    }
    
    
    private static long getProgramEndMillis(TranscodeRequest request, SingleTranscodingContext context) {
        long programStartMillis = getProgramMillis(request,
                                                   request.getProgramBroadcast().getTimeStop(),
                                                   context.getEndOffsetPSWithTVMeter(),
                                                   context.getEndOffsetPS(),
                                                   context.getEndOffsetWAV(),
                                                   context.getEndOffsetTSWithTVMeter(),
                                                   context.getEndOffsetTS(),
                                                   request.getAdditionalEndOffset());
        return programStartMillis;
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
