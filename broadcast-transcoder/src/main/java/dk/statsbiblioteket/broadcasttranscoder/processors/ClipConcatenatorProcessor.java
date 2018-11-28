package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Calculates the command to cut and concatenate the source files (excluding the initial "cat"). Only used by the multistreamTranscoder
 * @see MultistreamVideoTranscoderProcessor
 */
public class ClipConcatenatorProcessor extends ProcessorChainElement  {

    private static Logger logger = LoggerFactory.getLogger(ClipConcatenatorProcessor.class);

    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        Long blocksize = 1880L;
        Long offsetBytes = 0L;
        String processSubstitutionFileList = "";
        final int clipSize = request.getClips().size();
        for (int iclip = 0; iclip < clipSize; iclip++ ) {
            TranscodeRequest.FileClip clip = request.getClips().get(iclip);
            Long clipLength = clip.getClipLength();
            if (iclip == 0) {
                if (clip.getStartOffsetBytes() == null) {
                    offsetBytes = 0L;
                } else {
                    offsetBytes = clip.getStartOffsetBytes();
                }
                if (offsetBytes == null || offsetBytes < 0) offsetBytes = 0L;
                if (clipLength != null && clipSize == 1) {
    
                    processSubstitutionFileList += MessageFormat.format(" <(dd if=''{0}'' bs={1} skip={2} count={3}) ",
                                                                        clip.getFilepath(),
                                                                        blocksize,
                                                                        offsetBytes / blocksize,
                                                                        clipLength / blocksize);
                } else {         //Otherwise always go to end of file
                    processSubstitutionFileList += MessageFormat.format(" <(dd if=''{0}'' bs={1} skip={2}) ",
                                                                        clip.getFilepath(),
                                                                        blocksize,
                                                                        offsetBytes / blocksize);
                }

            } else if (iclip == clipSize - 1) {   //last clip in multiclip program
                String skipString = "";
                if (clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0L) {
                    logger.warn("Found non-zero offset outside first clip for '" + request.getObjectPid());
                    skipString = " skip=" + (clip.getStartOffsetBytes())/blocksize + " ";
                }
                if (clipLength != null) {
                    processSubstitutionFileList += MessageFormat.format(" <(dd if=''{0}'' bs={1}{2} count={3}) ",
                                                                        clip.getFilepath(),
                                                                        blocksize,
                                                                        skipString,
                                                                        clipLength / blocksize);
                } else {
                    processSubstitutionFileList += MessageFormat.format(" <(dd if=''{0}'' bs={1}{2}) ",
                                                                        clip.getFilepath(),
                                                                        blocksize,
                                                                        skipString);
                }
            } else {   //A file in the middle of a program so take the whole file
                String skipString = "";
                if (clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0L) {
                    logger.warn("Found non-zero offset outside first clip for '" + request.getObjectPid());
                    skipString = MessageFormat.format(" skip={0} ", clip.getStartOffsetBytes() / blocksize);
                }
                processSubstitutionFileList += MessageFormat.format(" <(dd if=''{0}'' bs={1}{2}) ",
                                                                    clip.getFilepath(),
                                                                    blocksize,
                                                                    skipString);
            }
        }
        request.setClipperCommand(processSubstitutionFileList);
    }
}
