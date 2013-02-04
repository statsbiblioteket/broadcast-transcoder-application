package dk.statsbiblioteket.broadcasttranscoder.processors;


import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.*;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Calculated holes and overlaps from the clips. Creates the ProgramStructure element
 */
public class CoverageAnalyserProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(CoverageAnalyserProcessor.class);
    private static int gapToleranceSeconds;


    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        gapToleranceSeconds = context.getGapToleranceSeconds();
        ProgramStructure localStructure = new ProgramStructure();
        request.setLocalProgramStructure(localStructure);
        findMissingStart(request, context, localStructure);
        findMissingEnd(request, context, localStructure);
        findHolesAndOverlaps(request, context, localStructure);
    }

    private void findMissingStart(TranscodeRequest request, InfrastructureContext context, ProgramStructure localProgramStructure) {
        long fileStartTime = request.getClips().get(0).getFileStartTime()/1000L;
        long programStartTime = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart())/1000L;
        final long missing = fileStartTime - programStartTime;
        if ( missing > gapToleranceSeconds) {
            logger.info("Program " + request.getObjectPid() + " is missing " + missing + " seconds" +
                    "at start");
            MissingStart ms = new MissingStart();
            ms.setMissingSeconds((int) missing);
            localProgramStructure.setMissingStart(ms);
        }
    }

    private void findMissingEnd(TranscodeRequest request, InfrastructureContext context, ProgramStructure localProgramStructure) {
        int iclips = request.getClips().size();
        long fileEndTime = request.getClips().get(iclips-1).getFileEndTime()/1000L;
        long programEndTime = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop())/1000L;
        final long missing = programEndTime - fileEndTime;
        if (missing > gapToleranceSeconds) {
            logger.info("Program " + request.getObjectPid() + " is missing " + missing + " seconds" +
                    "at end");
            MissingEnd me = new MissingEnd();
            me.setMissingSeconds((int) missing);
            localProgramStructure.setMissingEnd(me);
        }
    }

    private void findHolesAndOverlaps(TranscodeRequest request, InfrastructureContext context, ProgramStructure localProgramStructure) throws ProcessorException {
        ProgramStructure.Holes holes = new ProgramStructure.Holes();
        localProgramStructure.setHoles(holes);
        ProgramStructure.Overlaps overlaps = new ProgramStructure.Overlaps();
        localProgramStructure.setOverlaps(overlaps);
        Map.Entry<String, BroadcastMetadata> firstEntry = null;
        Map.Entry<String, BroadcastMetadata> secondEntry = null;
        for (BroadcastMetadata metadata: request.getBroadcastMetadata()) {
            Map.Entry<String, BroadcastMetadata> entry= getPidFromBroadcastMetadata(metadata, request);
            firstEntry = secondEntry;
            secondEntry = entry;
            if (firstEntry != null) {
                long firstEntryEnd = CalendarUtils.getTimestamp(firstEntry.getValue().getStopTime())/1000L;
                long secondEntryStart = CalendarUtils.getTimestamp(secondEntry.getValue().getStartTime())/1000L;
                long holeLength = secondEntryStart - firstEntryEnd;
                long overlapLength = firstEntryEnd - secondEntryStart;
                if (holeLength > gapToleranceSeconds) {
                    Hole hole = new Hole();
                    hole.setHoleLength(holeLength);
                    hole.setFile1UUID(firstEntry.getKey());
                    hole.setFile2UUID(secondEntry.getKey());
                    holes.getHole().add(hole);
                    logger.debug("Added a hole for " + request.getObjectPid() + " " + hole.toString());
                }
                if (overlapLength > gapToleranceSeconds) {
                    Overlap overlap = new Overlap();
                    overlap.setOverlapLength(overlapLength);
                    overlap.setFile1UUID(firstEntry.getKey());
                    overlap.setFile2UUID(secondEntry.getKey());
                    long programStartTime = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart())/1000L;
                    long programEndTime = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop())/1000L;
                    if (programStartTime < secondEntryStart && programEndTime > firstEntryEnd) {
                        overlap.setOverlapType(0);
                    } else if (programStartTime < secondEntryStart && programEndTime < firstEntryEnd) {
                        overlap.setOverlapType(1);
                    } else if (programStartTime > secondEntryStart && programEndTime < firstEntryEnd) {
                        overlap.setOverlapType(2);
                    } else if (programStartTime > secondEntryStart && programEndTime > firstEntryEnd) {
                        overlap.setOverlapType(3);
                    }
                    overlaps.getOverlap().add(overlap);
                    logger.debug("program start:" + programStartTime + " " + request.getProgramBroadcast().getTimeStart());
                    logger.debug("program end  :" + programEndTime + " " + request.getProgramBroadcast().getTimeStop());
                    logger.debug("first file end :" + firstEntryEnd + " " + firstEntry.getValue().getStopTime());
                    logger.debug("second file start :" + secondEntryStart + " " + secondEntry.getValue().getStartTime());
                    logger.debug("Added an overlap for " + request.getObjectPid()+ " " + overlap.toString());
                }
            }
        }

    }

    private static Map.Entry<String, BroadcastMetadata> getPidFromBroadcastMetadata(BroadcastMetadata metadata, TranscodeRequest request) throws ProcessorException {
        for (Map.Entry<String,BroadcastMetadata> entry: request.getPidMap().entrySet() ) {
            if (entry.getValue() == metadata) {
                return entry;
            }
        }
        throw new ProcessorException("Could not find pid for " + metadata + " in pidmap");
    }

}
