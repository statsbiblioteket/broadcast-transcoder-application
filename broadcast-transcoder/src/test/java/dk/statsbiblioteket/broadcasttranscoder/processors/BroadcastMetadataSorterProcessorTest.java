package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.opentest4j.TestAbortedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class BroadcastMetadataSorterProcessorTest {

    @BeforeEach
    public void setUp() throws TestAbortedException {
        try {
            InetAddress.getByName("carme");
        } catch (UnknownHostException e) {
            throw new TestAbortedException();
        }
    }

    @Test
    public void testProcessThis() throws ProcessorException, DatatypeConfigurationException {
        TranscodeRequest request = new TranscodeRequest();
        BroadcastMetadata m1 = new BroadcastMetadata();
        BroadcastMetadata m2 = new BroadcastMetadata();
        BroadcastMetadata m3 = new BroadcastMetadata();
        BroadcastMetadata m4 = new BroadcastMetadata();
        m1.setStartTime(CalendarUtils.getCalendar());
        m2.setStartTime(CalendarUtils.getCalendar());
        m3.setStartTime(CalendarUtils.getCalendar());
        m4.setStartTime(CalendarUtils.getCalendar());
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

    @Test
    public void testProcessThisOne() throws ProcessorException, DatatypeConfigurationException {
        TranscodeRequest request = new TranscodeRequest();
        BroadcastMetadata m1 = new BroadcastMetadata();

        m1.setStartTime(CalendarUtils.getCalendar());

        m1.getStartTime().setYear(100);

        List<BroadcastMetadata> bm = new ArrayList<BroadcastMetadata>();
        bm.add(m1);

        request.setBroadcastMetadata(bm);
        new BroadcastMetadataSorterProcessor().processThis(request, null);

    }

    @Test
    public void testSort() throws ProcessorException {
        SingleTranscodingContext context = new SingleTranscodingContext();

        context.setDomsEndpoint("http://carme:7880/centralWebservice-service/central/");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsPassword("spD68ZJl");
        TranscodeRequest request = new TranscodeRequest();
        request.setObjectPid("uuid:3a1bdfce-497f-4bff-8804-22ac000cca83");

        ProcessorChainElement programFetcher = new ProgramMetadataFetcherProcessor();
        ProcessorChainElement filedataFetcher    = new FileMetadataFetcherProcessor();
        ProcessorChainElement sorter = new BroadcastMetadataSorterProcessor();
        programFetcher.setChildElement(filedataFetcher);
        filedataFetcher.setChildElement(sorter);
        programFetcher.processIteratively(request, context);
    }

}
