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
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class WavTranscoderProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(WavTranscoderProcessor.class);


    public WavTranscoderProcessor() {
    }

    public WavTranscoderProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        String command = getMultiClipCommand(request, context);
        File outputFile = FileUtils.getTemporaryMediaOutputFile(request, context);
        FileUtils.getTemporaryMediaOutputDir(request, context).mkdirs();
        try {
            long timeout = MetadataUtils.getTimeout(request, context);
            logger.debug("Setting transcoding timeout for '" + request.getObjectPid() + "' to " + timeout + "ms");
            request.setTranscoderCommand(command);
            ExternalJobRunner.runClipperCommand(timeout, command);
        } catch (ExternalProcessTimedOutException e) {
            FileUtils.deleteAndLogFailedFile(outputFile, e);
            throw new ProcessorException("transcoding timeout for '" + request.getObjectPid(),e);
        }
    }

    private String getMultiClipCommand(TranscodeRequest request, SingleTranscodingContext context) {
        String command = "cat ";
        List<TranscodeRequest.FileClip> clips = request.getClips();
        long bitrate = request.getBitrate();
        for (int i=0; i<clips.size(); i++) {
            TranscodeRequest.FileClip clip = clips.get(i);
            String soxTranscodeParameters = context.getSoxTranscodeParams();
            command += " <(sox '" + clip.getFilepath() + "' " + soxTranscodeParameters + " - ";
            if ((clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0) || clip.getClipLength() != null) {
                String trimFilter = " trim ";
                if (clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0) {
                    long startOffsetSeconds = clip.getStartOffsetBytes()/bitrate;
                    if (i == 0) {
                        startOffsetSeconds = Math.max(0, startOffsetSeconds);
                    }
                    trimFilter += startOffsetSeconds + ".0 ";
                } else {
                    trimFilter += " 0.0 ";
                }
                if (clip.getClipLength() != null) {
                    long clipLengthSeconds = clip.getClipLength()/bitrate;
                    if ( i==0 ) {
                        clipLengthSeconds = clipLengthSeconds;
                    }
                    trimFilter += clipLengthSeconds + ".0 ";
                }
                command += trimFilter;
            }
            command += " ) ";
        }
        //TODO this ffmpeg command in config
        command += "| ffmpeg -i - "
                + " -ac 2  -ab " + context.getAudioBitrate() + "000 -y "
                + FileUtils.getTemporaryMediaOutputFile(request, context).getAbsolutePath();
        return command;
    }

}
