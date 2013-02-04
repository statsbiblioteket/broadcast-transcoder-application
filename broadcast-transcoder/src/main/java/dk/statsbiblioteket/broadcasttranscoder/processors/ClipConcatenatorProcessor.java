package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates the command to cut and concatenate the source files (excluding the initial "cat").
 */
public class ClipConcatenatorProcessor extends ProcessorChainElement  {

    private static Logger logger = LoggerFactory.getLogger(ClipConcatenatorProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        Long blocksize = 1880L;
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
                    Long totalLengthBytes = clipLength;   //Program contained within file
                    processSubstitutionFileList += " <(dd if=" + clip.getFilepath() + " bs="+blocksize + " skip=" + offsetBytes/blocksize
                            + " count=" + totalLengthBytes/blocksize + ") " ;
                } else {         //Otherwise always go to end of file
                    processSubstitutionFileList += " <(dd if=" + clip.getFilepath() + " bs="+blocksize + " skip=" + offsetBytes/blocksize + ") " ;
                }

            } else if (iclip == clipSize - 1 && clipSize != 1) {   //last clip in multiclip program
                String skipString = "";
                if (clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0L) {
                    logger.warn("Found non-zero offset outside first clip for '" + request.getObjectPid());
                    skipString = " skip=" + (clip.getStartOffsetBytes())/blocksize + " ";
                }
                if (clipLength != null) {
                    processSubstitutionFileList +=" <(dd if=" + clip.getFilepath() + " bs=" + blocksize + skipString +  " count=" + clipLength/blocksize + ") ";
                } else {
                    processSubstitutionFileList +=" <(dd if=" + clip.getFilepath() + " bs=" + blocksize + skipString  + ") ";
                }
            } else {   //A file in the middle of a program so take the whole file
                String skipString = "";
                if (clip.getStartOffsetBytes() != null && clip.getStartOffsetBytes() != 0L) {
                    logger.warn("Found non-zero offset outside first clip for '" + request.getObjectPid());
                    skipString = " skip=" + clip.getStartOffsetBytes()/blocksize + " ";
                }
                processSubstitutionFileList += " <(dd if=" + clip.getFilepath() + " bs=" + blocksize + skipString + ") ";
            }
        }
        request.setClipperCommand(processSubstitutionFileList);
    }
}
