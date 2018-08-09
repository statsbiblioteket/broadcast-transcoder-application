package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;

import java.util.Collections;
import java.util.List;

/**
 * The processor sorts the BroadcastMetadata according the startTime
 */
public class BroadcastMetadataSorterProcessor extends ProcessorChainElement {


    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        List<BroadcastMetadata> broadcastMetadata = request.getBroadcastMetadata();
        int size = broadcastMetadata.size();
        boolean done = true;
        do {
            done = true;
            for (int i=0; i < size-1; i++) {
                BroadcastMetadata element1 = broadcastMetadata.get(i);
                long time1 = element1.getStartTime().toGregorianCalendar().getTimeInMillis();
                BroadcastMetadata element2 = broadcastMetadata.get(i+1);
                long time2 = element2.getStartTime().toGregorianCalendar().getTimeInMillis();
                if (time2 < time1) {
                    done = false;
                    Collections.swap(broadcastMetadata, i, i+1);
                }
            }
        } while (!done);
    }
}
