package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
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
public class MultistreamVideoTranscoderProcessor extends ProcessorChainElement {


    public static int getHeight(TranscodeRequest request, SingleTranscodingContext context) {
        return context.getVideoHeight();
    }

    public static int getWidth(TranscodeRequest request, SingleTranscodingContext context) {
        Double aspectRatio = request.getDisplayAspectRatio();
        if (aspectRatio != null) {
            long width = 2*Math.round(aspectRatio*getHeight(request, context)/2);
            //if (width%2 == 1) width += 1;
            return (int) width;
        } else {
            return 320;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(MultistreamVideoTranscoderProcessor.class);


    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        int programNumber = 0;
        if (request.getFileFormat().equals(FileFormatEnum.MULTI_PROGRAM_MUX)) {
            Integer programNumberObject = request.getClips().get(0).getProgramId();
            if (programNumberObject == null) {
                throw new ProcessorException("Cannot transcode multi-program transport stream because no program number specified: " + request.getObjectPid());
            } else {
                programNumber = programNumberObject;
            }
        }
        String processSubstitutionFileList = request.getClipperCommand();
        File outputDir = FileUtils.getTemporaryMediaOutputDir(request, context);
        outputDir.mkdirs();


        String clipperCommand = null;
        if (request.getFileFormat().equals(FileFormatEnum.SINGLE_PROGRAM_AUDIO_TS)) {
            clipperCommand = findAudioClipperCommand(request, context, processSubstitutionFileList);
        } else {
            clipperCommand = findVideoClipperCommand(request, context, processSubstitutionFileList);
        }
        try {
            long programLength = MetadataUtils.findProgramLengthMillis(request);
            long timeout = (long) (programLength/context.getTranscodingTimeoutDivisor());
            logger.debug("Setting transcoding timeout for '" + request.getObjectPid() + "' to " + timeout + "ms" );
            request.setTranscoderCommand(clipperCommand);
            ExternalJobRunner.runClipperCommand(timeout, clipperCommand);
        } catch (ExternalProcessTimedOutException e) {
            File outputFile =  FileUtils.getTemporaryMediaOutputFile(request, context);
            logger.warn("Deleting '" + outputFile.getAbsolutePath() + "'");
            outputFile.delete();
            throw new ProcessorException("Process timed out for "+request.getObjectPid(),e);
        }
    }

    private String findAudioClipperCommand(TranscodeRequest request, SingleTranscodingContext context, String processSubstitutionFileList) {
        return "cat " + processSubstitutionFileList + "| "
                + "ffmpeg -i - -acodec libmp3lame -ar 44100 -ac 2 -ab "
                + context.getAudioBitrate() + "000 -y " + FileUtils.getTemporaryMediaOutputDir(request, context);
    }

    private String findVideoClipperCommand(TranscodeRequest request, SingleTranscodingContext context, String processSubstitutionFileList) throws ProcessorException {
        String programSelector = " --program=1010 --sout-all --ts-extra-pmt=1010:1010=" + request.getVideoPid() + ":video=" + request.getVideoFcc()
                + "," + request.getMinimumAudioPid() + ":audio=" + request.getAudioFcc();
        if (request.getDvbsubPid() != null) {
            programSelector += "," + request.getDvbsubPid() + ":spu=dvbs";
        }
        logger.debug("Using Custom PMT for '" + request.getObjectPid() + "': " + programSelector);
        String transcodingString = context.getVlcTranscodingString();
        transcodingString = transcodingString.replace("$$VIDEO_BITRATE$$", context.getVideoBitrate()+"");
        transcodingString = transcodingString.replace("$$VIDEO_WIDTH$$", getWidth(request, context)+"");
        transcodingString = transcodingString.replace("$$VIDEO_HEIGHT$$", getHeight(request, context)+"");
        transcodingString = transcodingString.replace("$$AUDIO_BITRATE$$", context.getAudioBitrate()+"");
        transcodingString = transcodingString.replace("$$OUTPUT_FILE$$", FileUtils.getTemporaryMediaOutputFile(request, context).getAbsolutePath());
        transcodingString = transcodingString.replace("$$DISPLAY_ASPECT$$", request.getDisplayAspectRatioString());
        if (transcodingString.contains("$$")) {
            throw new ProcessorException("Failed to set all template variables in transcoding string: " + transcodingString);
        }

        String  clipperCommand = "cat " + processSubstitutionFileList + " |" + transcodingString ;
        return clipperCommand;
    }


}
