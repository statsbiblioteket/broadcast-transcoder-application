package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 */
public class BroadcastMetadataSorterProcessor extends ProcessorChainElement {


    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        List<BroadcastMetadata> broadcastMetadata = request.getBroadcastMetadata();
        int size = broadcastMetadata.size();
        boolean done = true;
        do {
            done = true;
            for (int i=0; i < size-1; i++) {
                BroadcastMetadata element1 = broadcastMetadata.get(i);
                long time1 = element1.getStartTime();
                BroadcastMetadata element2 = broadcastMetadata.get(i+1);
                long time2 = element2.getStartTime();
                if (time2 < time1) {
                    done = false;
                    Collections.swap(broadcastMetadata, i, i+1);
                }
            }
        } while (!done);
    }
}
