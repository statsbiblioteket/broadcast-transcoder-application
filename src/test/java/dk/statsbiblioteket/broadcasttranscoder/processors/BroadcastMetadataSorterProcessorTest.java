package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BroadcastMetadataSorterProcessorTest extends TestCase {

    public void testProcessThis() throws ProcessorException {
        TranscodeRequest request = new TranscodeRequest();
        BroadcastMetadata m1 = new BroadcastMetadata();
        BroadcastMetadata m2 = new BroadcastMetadata();
        BroadcastMetadata m3 = new BroadcastMetadata();
        BroadcastMetadata m4 = new BroadcastMetadata();
        m1.setStartTime(100l);
        m2.setStartTime(300l);
        m3.setStartTime(200l);
        m4.setStartTime(400l);
        List<BroadcastMetadata> bm = new ArrayList<BroadcastMetadata>();
        bm.add(m1);
        bm.add(m2);
        bm.add(m3);
        bm.add(m4);
        request.setBroadcastMetadata(bm);
        new BroadcastMetadataSorterProcessor().processThis(request, null);
        assertEquals(100l, bm.get(0).getStartTime().longValue());
        assertEquals(200l, bm.get(1).getStartTime().longValue());
        assertEquals(300l, bm.get(2).getStartTime().longValue());
        assertEquals(400l, bm.get(3).getStartTime().longValue());
        bm = new ArrayList<BroadcastMetadata>();
        bm.add(m4);
        bm.add(m3);
        bm.add(m2);
        bm.add(m1);
        request.setBroadcastMetadata(bm);
        new BroadcastMetadataSorterProcessor().processThis(request, null);
        assertEquals(100l, bm.get(0).getStartTime().longValue());
        assertEquals(200l, bm.get(1).getStartTime().longValue());
        assertEquals(300l, bm.get(2).getStartTime().longValue());
        assertEquals(400l, bm.get(3).getStartTime().longValue());
        bm = new ArrayList<BroadcastMetadata>();
        bm.add(m1);
        bm.add(m4);
        bm.add(m3);
        bm.add(m2);
        request.setBroadcastMetadata(bm);
        new BroadcastMetadataSorterProcessor().processThis(request, null);
        assertEquals(100l, bm.get(0).getStartTime().longValue());
        assertEquals(200l, bm.get(1).getStartTime().longValue());
        assertEquals(300l, bm.get(2).getStartTime().longValue());
        assertEquals(400l, bm.get(3).getStartTime().longValue());

    }

}
