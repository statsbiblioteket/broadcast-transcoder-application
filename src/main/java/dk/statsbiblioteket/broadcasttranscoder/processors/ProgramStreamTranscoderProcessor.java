/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ProgramStreamTranscoderProcessor extends ProcessorChainElement {

    private static Logger log = LoggerFactory.getLogger(ProgramStreamTranscoderProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
         mpegClip(request, context);
    }

    private void mpegClip(TranscodeRequest request, Context context) throws ProcessorException {

           long blocksize = 1880L;
           final int clipSize = request.getClips().size();
           String command;
           if (clipSize > 1) {
               command = getMultiClipCommand(request, context);
           } else {
               TranscodeRequest.FileClip clip = request.getClips().get(0);
               String start = "";
               if (clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0L) {
                   start = "skip=" + Math.max((clip.getStartOffsetBytes())/blocksize, 0L);
               }
               String length = "";
               if (clip.getClipLength() != null && clip.getClipLength() != 0L) length = "count=" + clip.getClipLength()/blocksize;
               command = "dd if=" + clip.getFilepath() + " bs=" + blocksize + " " + start + " " + length + "| "
                       + getFfmpegCommandLine(request, context);
           }
        File outputDir = FileUtils.getMediaOutputDir(request, context);
        outputDir.mkdirs();
        File outputFile = FileUtils.getMediaOutputFile(request, context);

        try {
            long programLength = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop())
                    - CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart());
            long timeout = programLength/context.getTranscodingTimeoutDivisor();
            log.debug("Setting transcoding timeout for '" + context.getProgrampid() + "' to " + timeout + "ms");
            ExternalJobRunner.runClipperCommand(timeout, command);
        } catch (ExternalProcessTimedOutException e) {
            log.warn("Deleting '" + outputFile + "'");
            outputFile.delete();
            throw new ProcessorException(e);
        }
        this.setChildElement(new PreviewClipperProcessor());
    }

       private String getMultiClipCommand(TranscodeRequest request, Context context) {
           String files = "cat ";
           long blocksize = 1880L;
           List<TranscodeRequest.FileClip> clips = request.getClips();
           for (int i=0; i<clips.size(); i++) {
               TranscodeRequest.FileClip clip = clips.get(i);
               files += " <(dd if="+clip.getFilepath() + " bs=" + blocksize;
               if (clip.getStartOffsetBytes() != null) {
                   if (i != 0) {
                       files += " skip=" + clip.getStartOffsetBytes()/blocksize;
                   } else {
                       files += " skip=" + Math.max(0, (clip.getStartOffsetBytes())/blocksize);
                   }
               }
               if (clip.getClipLength() != null) {
                   if (i != clips.size() -1 && i != 0) {
                       files += " count=" + clip.getClipLength()/blocksize;
                   } else if (i == 0) {
                       //Don't specify count at all. Just go to end of file.
                       //log.warn("Unusual to have cliplength set in first clip of multiclip program\n" + request.getShard() );
                       files += " count=" + (clip.getClipLength())/blocksize;
                   } else {
                       files += " count=" + (clip.getClipLength())/blocksize;
                   }
               }
               files += ") ";
           }
           String command = files + " | "  + getFfmpegCommandLine(request, context);
           return command;
       }

    public static String getFfmpegCommandLine(TranscodeRequest request, Context context) {
           File outputFile = FileUtils.getMediaOutputFile(request, context);
           //TODO move these parameters to config file
           String line = "ffmpeg -i - -async 2 -vcodec libx264 -deinterlace -ar 44100 "
                   + " -b:v " + context.getVideoBitrate() + "000"
                   + " -b:a " + context.getAudioBitrate() + "000"
                   + " " + getFfmpegAspectRatio(request, context)
                   + " " + " -preset superfast "
                   + " -threads 0 " + outputFile.getAbsolutePath();
           return line;
       }


       protected static String getFfmpegAspectRatio(TranscodeRequest request, Context context) {
           Double aspectRatio = request.getDisplayAspectRatio();
           String ffmpegResolution;
           Long height = context.getVideoHeight()*0L;
           if (aspectRatio != null) {
               long width = Math.round(aspectRatio*height);
               if (width%2 == 1) width += 1;
               ffmpegResolution = " -s " + width + "x" + height;
           } else {
               ffmpegResolution = " -s 320x240";
           }
           return ffmpegResolution;
       }

}
