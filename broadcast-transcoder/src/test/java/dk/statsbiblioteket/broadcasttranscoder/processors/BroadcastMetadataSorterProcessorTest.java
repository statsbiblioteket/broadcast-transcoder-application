package dk.statsbiblioteket.broadcasttranscoder.processors;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
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
        m1.setStartTime(new XMLGregorianCalendarImpl());
        m2.setStartTime(new XMLGregorianCalendarImpl());
        m3.setStartTime(new XMLGregorianCalendarImpl());
        m4.setStartTime(new XMLGregorianCalendarImpl());
        m1.getStartTime().setYear(100);
        m2.getStartTime().setYear(200);
        m3.getStartTime().setYear(300);
        m4.getStartTime().setYear(400);
        List<BroadcastMetadata> bm = new ArrayList<BroadcastMetadata>();
        bm.add(m1);
        bm.add(m2);
        bm.add(m3);
        bm.add(m4);
        request.setBroadcastMetadata(bm);
        new BroadcastMetadataSorterProcessor().processThis(request, null);
        assertEquals(100, bm.get(0).getStartTime().getYear());
        assertEquals(200, bm.get(1).getStartTime().getYear());
        assertEquals(300, bm.get(2).getStartTime().getYear());
        assertEquals(400, bm.get(3).getStartTime().getYear());
        bm = new ArrayList<BroadcastMetadata>();
        bm.add(m4);
        bm.add(m3);
        bm.add(m2);
        bm.add(m1);
        request.setBroadcastMetadata(bm);
        new BroadcastMetadataSorterProcessor().processThis(request, null);
        assertEquals(100, bm.get(0).getStartTime().getYear());
              assertEquals(200, bm.get(1).getStartTime().getYear());
              assertEquals(300, bm.get(2).getStartTime().getYear());
              assertEquals(400, bm.get(3).getStartTime().getYear());
        bm = new ArrayList<BroadcastMetadata>();
        bm.add(m1);
        bm.add(m4);
        bm.add(m3);
        bm.add(m2);
        request.setBroadcastMetadata(bm);
        new BroadcastMetadataSorterProcessor().processThis(request, null);
        assertEquals(100, bm.get(0).getStartTime().getYear());
              assertEquals(200, bm.get(1).getStartTime().getYear());
              assertEquals(300, bm.get(2).getStartTime().getYear());
              assertEquals(400, bm.get(3).getStartTime().getYear());
    }

}
