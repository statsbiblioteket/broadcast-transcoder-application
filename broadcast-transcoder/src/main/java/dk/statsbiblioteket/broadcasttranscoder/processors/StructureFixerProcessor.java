/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Fixing overlaps by modifying the clips so that nothing is duplicated.
 */
public class StructureFixerProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(StructureFixerProcessor.class);


    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        handleMissingStart(request, context);
        handleMissingEnd(request, context);
        handleHoles(request, context);
        handleOverlaps(request, context);
    }

    private void handleOverlaps(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        ProgramStructure.Overlaps overlaps = request.getLocalProgramStructure().getOverlaps();
        if (overlaps == null) {
            return;
        }
        List<Overlap> overlapList = overlaps.getOverlap();
        if (overlapList == null || overlapList.isEmpty()) {
            return;
        }
        for (Overlap overlap: overlapList) {
            handleSingleOverlap(request, overlap);
        }
    }

    private void handleSingleOverlap(TranscodeRequest request, Overlap overlap) throws ProcessorException {
        BroadcastMetadata bmd1 = request.getPidMap().get(overlap.getFile1UUID());
        BroadcastMetadata bmd2 = request.getPidMap().get(overlap.getFile2UUID());
        File file1 = request.getFileMap().get(bmd1);
        File file2 = request.getFileMap().get(bmd2);
        TranscodeRequest.FileClip clip1 = null;
        TranscodeRequest.FileClip clip2 = null;
        for (TranscodeRequest.FileClip clip: request.getClips()) {
            String clipFilename = (new File(clip.getFilepath())).getName();
            if (clipFilename.equals(file1.getName())) {
                clip1 = clip;
            } else
            if (clipFilename.equals(file2.getName())) {
                clip2 = clip;
            }
        }
        if (clip1 == null) {
            logger.debug("Could not find {} in clip, although it appears in overlap {}. Proceeding with caution.", file1.getAbsolutePath(), overlap.toString());
            return;
        }
        if (clip2 == null) {
            logger.debug("Could not find {} in clip, although it appears in overlap {}. Proceeding with caution.", file2.getAbsolutePath(), overlap.toString());
            return;
        }
        switch (overlap.getOverlapType()) {
            case(0):
                final long startOffsetBytes = overlap.getOverlapLength() * request.getBitrate();
                logger.info("Fixing overlap '" + overlap + "' by setting start offset to '" + startOffsetBytes + " bytes' in" +
                        " file '" + clip2.getFilepath() + "'");
                clip2.setStartOffsetBytes(startOffsetBytes);
                break;
            case(1):
                logger.info("Fixing overlap '" + overlap + "' by removing '" + clip2.getFilepath() + "'");
                request.getClips().remove(clip2);
                break;
            case(2):
                logger.info("Fixing overlap '" + overlap + "' by removing '" + clip2.getFilepath() + "'");
                request.getClips().remove(clip2);
                break;
            case(3):
                logger.info("Fixing overlap '" + overlap + "' by removing '" + clip1.getFilepath() + "'");
                request.getClips().remove(clip1);
                break;
        }
    }

    private void handleMissingStart(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        MissingStart missingStart = request.getLocalProgramStructure().getMissingStart();
        if (missingStart != null && missingStart.getMissingSeconds() > context.getMaxMissingStart()) {
            final String s = missingStart.getMissingSeconds() + " missing seconds at start for " +
                    request.getObjectPid() + " is more than permitted. Exiting.";
            logger.info(s);
            request.setRejected(true);
            this.setChildElement(null);
        }
    }

    private void handleMissingEnd(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        MissingEnd missingEnd = request.getLocalProgramStructure().getMissingEnd();
        if (missingEnd != null && missingEnd.getMissingSeconds() > context.getMaxMissingEnd()) {
            final String s = missingEnd.getMissingSeconds() + " missing seconds at end for " +
                    request.getObjectPid() + " is more than permitted. Exiting.";
            logger.info(s);
            request.setRejected(true);
            this.setChildElement(null);
        }
    }

    private void handleHoles(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        ProgramStructure.Holes holes = request.getLocalProgramStructure().getHoles();
        if (holes == null) {
            return;
        }
        List<Hole> holeList = holes.getHole();
        if (holeList == null || holeList.isEmpty()) {
            return;
        } else {
            for (Hole hole: holeList) {
                BroadcastMetadata bmd1 = request.getPidMap().get(hole.getFile1UUID());
                BroadcastMetadata bmd2 = request.getPidMap().get(hole.getFile2UUID());
                File file1 = request.getFileMap().get(bmd1);
                File file2 = request.getFileMap().get(bmd2);
                TranscodeRequest.FileClip clip1 = null;
                TranscodeRequest.FileClip clip2 = null;
                for (TranscodeRequest.FileClip clip: request.getClips()) {
                    String clipFilename = (new File(clip.getFilepath())).getName();
                    if (clipFilename.equals(file1.getName())) {
                        clip1 = clip;
                    } else
                    if (clipFilename.equals(file2.getName())) {
                        clip2 = clip;
                    }
                }
                if (clip1 == null) {
                    logger.debug("Could not find {} in clip, although it appears in hole {}. Proceeding with caution.", file1.getAbsolutePath(), hole.toString());
                    return;
                } else if (clip2 == null) {
                    logger.debug("Could not find {} in clip, although it appears in hole {}. Proceeding with caution.", file2.getAbsolutePath(), hole.toString());
                    return;
                } else if (hole.getHoleLength() > context.getMaxHole()) {
                    String s = "Hole length for " + hole.toString() + " is greater than maximum permitted. Exiting.";
                    logger.info(s);
                    request.setRejected(true);
                    this.setChildElement(null);
                }
            }
        }
    }


}
