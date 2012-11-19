package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.Hole;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.MissingEnd;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.MissingStart;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.Overlap;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramStructure;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/28/12
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class CoverageAnalyserProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(CoverageAnalyserProcessor.class);
    private static int gapToleranceSeconds;

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        gapToleranceSeconds = context.getGapToleranceSeconds();
        ProgramStructure localStructure = new ProgramStructure();
        findMissingStart(request, context, localStructure);
        findMissingEnd(request, context, localStructure);
        findHolesAndOverlaps(request, context, localStructure);
    }

    private void findMissingStart(TranscodeRequest request, Context context, ProgramStructure localProgramStructure) {
        long fileStartTime = request.getClips().get(0).getFileStartTime()/1000L;
        long programStartTime = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart())/1000L;
        final long missing = fileStartTime - programStartTime;
        if ( missing > gapToleranceSeconds) {
             logger.info("Program " + context.getProgrampid() + " is missing " + missing + " seconds" +
                     "at start");
            MissingStart ms = new MissingStart();
            ms.setMissingSeconds((int) missing);
            localProgramStructure.setMissingStart(ms);
        }
    }

    private void findMissingEnd(TranscodeRequest request, Context context, ProgramStructure localProgramStructure) {
        int iclips = request.getClips().size();
        long fileEndTime = request.getClips().get(iclips-1).getFileEndTime()/1000L;
        long programEndTime = CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop())/1000L;
        final long missing = programEndTime - fileEndTime;
        if (missing > gapToleranceSeconds) {
                logger.info("Program " + context.getProgrampid() + " is missing " + missing + " seconds" +
                     "at end");
            MissingEnd me = new MissingEnd();
            me.setMissingSeconds((int) missing);
            localProgramStructure.setMissingEnd(me);
        }
    }

    private void findHolesAndOverlaps(TranscodeRequest request, Context context, ProgramStructure localProgramStructure) {
        ProgramStructure.Holes holes = new ProgramStructure.Holes();
        localProgramStructure.setHoles(holes);
        ProgramStructure.Overlaps overlaps = new ProgramStructure.Overlaps();
        localProgramStructure.setOverlaps(overlaps);
        Map.Entry<String, BroadcastMetadata> firstEntry = null;
        Map.Entry<String, BroadcastMetadata> secondEntry = null;
        for(Map.Entry<String, BroadcastMetadata> entry: request.getPidMap().entrySet()) {
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
                    logger.debug("Added a hole for " + context.getProgrampid() + " " + hole.toString());
                }
                if (overlapLength > gapToleranceSeconds) {
                    Overlap overlap = new Overlap();
                    overlap.setOverlapLength(overlapLength);
                    overlap.setFile1UUId(firstEntry.getKey());
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
                    logger.debug("Adden an overlap for " + context.getProgrampid() + " " + overlap.toString());
                }
            }
        }

    }



}
