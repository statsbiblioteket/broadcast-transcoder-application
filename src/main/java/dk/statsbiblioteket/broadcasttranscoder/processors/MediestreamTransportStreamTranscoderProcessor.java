package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/26/12
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediestreamTransportStreamTranscoderProcessor extends ProcessorChainElement {

    public static int getHeight(TranscodeRequest request, Context context) {
        return context.getVideoHeight();
    }

    public static int getWidth(TranscodeRequest request, Context context) {
        Double aspectRatio = request.getDisplayAspectRatio();
        if (aspectRatio != null) {
            long width = Math.round(aspectRatio*getHeight(request, context));
            if (width%2 == 1) width += 1;
            return (int) width;
        } else {
            return 320;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(MediestreamTransportStreamTranscoderProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        Long blocksize = 1880L;
        Long offsetBytes = 0L;
        String processSubstitutionFileList = "";
        final int clipSize = request.getClips().size();
        int programNumber = 0;
        for (int iclip = 0; iclip < clipSize; iclip++ ) {
            TranscodeRequest.FileClip clip = request.getClips().get(iclip);
            Long clipLength = clip.getClipLength();
            if (iclip == 0) {
                programNumber = clip.getProgramId();
                if (clip.getStartOffsetBytes() == null) {
                    offsetBytes = 0L;
                } else {
                    offsetBytes = clip.getStartOffsetBytes();
                }
                if (offsetBytes == null || offsetBytes < 0) offsetBytes = 0L;
                if (clipLength != null && clipSize == 1) {
                    Long totalLengthBytes = clipLength;   //Program contained within file
                    processSubstitutionFileList += " <(dd if=" + clip.getFilepath() + " bs="+blocksize + " skip=" + offsetBytes/blocksize
                            + " count=" + totalLengthBytes/blocksize + ") " ;
                } else {         //Otherwise always go to end of file
                    processSubstitutionFileList += " <(dd if=" + clip.getFilepath() + " bs="+blocksize + " skip=" + offsetBytes/blocksize + ") " ;
                }

            } else if (iclip == clipSize - 1 && clipSize != 1) {   //last clip in multiclip program
                String skipString = "";
                if (clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0L) {
                    logger.warn("Found non-zero offset outside first clip for '" + context.getProgrampid());
                    skipString = " skip=" + (clip.getStartOffsetBytes())/blocksize + " ";
                }
                if (clipLength != null) {
                    processSubstitutionFileList +=" <(dd if=" + clip.getFilepath() + " bs=" + blocksize + skipString +  " count=" + clipLength/blocksize + ") ";
                } else {
                    processSubstitutionFileList +=" <(dd if=" + clip.getFilepath() + " bs=" + blocksize + skipString  + ") ";
                }
                //processSubstitutionFileList +=" <(dd if=" + clip.getFilepath() + " bs=" + blocksize + skipString +  " count=" + clipLength/blocksize + ") ";
            } else {   //A file in the middle of a program so take the whole file
                String skipString = "";
                if (clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0L) {
                    logger.warn("Found non-zero offset outside first clip for '" + context.getProgrampid());
                    skipString = " skip=" + clip.getStartOffsetBytes()/blocksize + " ";
                }
                processSubstitutionFileList += " <(dd if=" + clip.getFilepath() + " bs=" + blocksize + skipString + ") ";
            }
        }

        File outputDir = FileUtils.getMediaOutputDir(request, context);
        outputDir.mkdirs();

        //The odd logic here is that we prefer to use custom-PMT if we have all three pids, but because of a bug in vlc we
        //may need to use a custom PMT to get DR1 (program 101) to work even if we don't have a pid for dvbsub. So:
        boolean useCustomPMT = (request.getDvbsubPid() != null && !request.getAudioPids().isEmpty() && request.getVideoPid() != null && request.getVideoFcc() != null && request.getAudioFcc() != null );
        useCustomPMT = useCustomPMT |  (!request.getAudioPids().isEmpty() && request.getVideoPid() != null && request.getVideoFcc() != null && request.getAudioFcc() != null && programNumber==101);
        String clipperCommand = null;
        if (request.getFileFormat().equals(FileFormatEnum.AUDIO_WAV)) {
            clipperCommand = findAudioClipperCommand(request, context, processSubstitutionFileList);
        } else {
            clipperCommand = findVideoClipperCommand(request, context, processSubstitutionFileList, programNumber, useCustomPMT);
        }
        try {
            long programLength = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop())
                    - CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart());
            long timeout = programLength/context.getTranscodingTimeoutDivisor();
            logger.debug("Setting transcoding timeout for '" + context.getProgrampid() + "' to " + timeout + "ms" );
            ExternalJobRunner.runClipperCommand(timeout, clipperCommand);
        } catch (ExternalProcessTimedOutException e) {
            File outputFile =  FileUtils.getMediaOutputFile(request, context);
            logger.warn("Deleting '" + outputFile.getAbsolutePath() + "'");
            outputFile.delete();
            throw new ProcessorException(e);
        }
        this.setChildElement(new PreviewClipperProcessor());
    }

    private String findAudioClipperCommand(TranscodeRequest request, Context context, String processSubstitutionFileList) {
        return "cat " + processSubstitutionFileList + "| "
                + "ffmpeg -i - -acodec libmp3lame -ar 44100 -ab "
                + context.getAudioBitrate() + "000 " + FileUtils.getMediaOutputFile(request, context);
    }

    private String findVideoClipperCommand(TranscodeRequest request, Context context, String processSubstitutionFileList, int programNumber, boolean useCustomPMT) {
        String clipperCommand;
        if (!useCustomPMT) {
            clipperCommand = "cat " + processSubstitutionFileList + " | vlc - --program=" + programNumber + " --quiet --demux=ts --intf dummy --play-and-exit --noaudio --novideo "
                    + "--sout-all --sout '#duplicate{dst=\"transcode{senc=dvbsub}"
                    + ":transcode{vcodec=h264,vb=" + context.getVideoBitrate() + ",venc=x264{" + context.getX264Params() + "},soverlay,deinterlace,audio-sync,"
                    + ",width=" + getWidth(request, context)
                    + ",height=" + getHeight(request, context) +",threads=0}"
                    + ":std{access=file,mux=ts,dst=-}\""
                    + ",select=\"program=" + programNumber + "\"' | "
                    + "ffmpeg -i -  -async 2 -vcodec copy -ac 2 -acodec libmp3lame -ar 44100 -ab " + context.getAudioBitrate() + "000 -f flv " + FileUtils.getMediaOutputFile(request, context);
        } else {
            String programSelector = " --program=1010 --sout-all --ts-extra-pmt=1010:1010=" + request.getVideoPid() + ":video=" + request.getVideoFcc()
                    + "," + request.getMinimumAudioPid() + ":audio=" + request.getAudioFcc();
            if (request.getDvbsubPid() != null) {
                programSelector += "," + request.getDvbsubPid() + ":spu=dvbs";
            }
            logger.debug("Using Custom PMT for '" + context.getProgrampid() + "': " + programSelector);
            clipperCommand = "cat " + processSubstitutionFileList + " |  vlc - " + programSelector + " --quiet --demux=ts --intf dummy --play-and-exit --noaudio --novideo "
                    + "--sout-all --sout '#transcode{vcodec=x264,vb=" + context.getVideoBitrate() + ",venc=x264{" + context.getX264Params() + "}" +
                    ",soverlay,deinterlace,audio-sync,"
                    + ",width=" + getWidth(request, context)
                    + ",height=" + getHeight(request, context) +",threads=0}"
                    + ":std{access=file,mux=ts,dst=-}' |" +
                    "ffmpeg -i -  -async 2 -vcodec copy -acodec libmp3lame -ac 2 -ar 44100 -ab " + context.getAudioBitrate()
                    + "000 -f flv " + FileUtils.getMediaOutputFile(request, context);
        }
        return clipperCommand;
    }


}
