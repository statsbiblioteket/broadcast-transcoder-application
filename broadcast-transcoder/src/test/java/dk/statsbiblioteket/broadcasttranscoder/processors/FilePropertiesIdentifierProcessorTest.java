package dk.statsbiblioteket.broadcasttranscoder.processors;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * Test of file properties identifier.
 */
public class FilePropertiesIdentifierProcessorTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testProcessThis() throws Exception {
        TranscodeRequest request;
        FilePropertiesIdentifierProcessor filePropertiesIdentifierProcessor = new FilePropertiesIdentifierProcessor();

        request = getMockRequest("file.ts");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals("Should have right file type", FileFormatEnum.SINGLE_PROGRAM_AUDIO_TS, request.getFileFormat());
        assertEquals("Should have right bit rate", 0, request.getBitrate());

        request = getMockRequest("file.mpg");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals("Should have right file type", FileFormatEnum.MPEG_PS, request.getFileFormat());
        assertEquals("Should have right bit rate", 0, request.getBitrate());

        request = getMockRequest("file.mpeg");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals("Should have right file type", FileFormatEnum.MPEG_PS, request.getFileFormat());
        assertEquals("Should have right bit rate", 0, request.getBitrate());

        request = getMockRequest("file.mpeg1");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals("Should have right file type", FileFormatEnum.MPEG_PS, request.getFileFormat());
        assertEquals("Should have right bit rate", 0, request.getBitrate());

        request = getMockRequest("file.wav");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals("Should have right file type", FileFormatEnum.AUDIO_WAV, request.getFileFormat());
        assertEquals("Should have right bit rate", 0, request.getBitrate());

        request = getMockRequest("muxfile.ts");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals("Should have right file type", FileFormatEnum.MULTI_PROGRAM_MUX, request.getFileFormat());
        assertEquals("Should have right bit rate", 0, request.getBitrate());

        request = getMockRequest("file.txt");
        try {
            filePropertiesIdentifierProcessor.processThis(request, null);
            fail("Should throw excpetion on unknown file type");
        } catch (ProcessorException e) {
            // Expected
        }
    }

    private TranscodeRequest getMockRequest(String filename) throws DatatypeConfigurationException {
        TranscodeRequest request;
        request = new TranscodeRequest();
        Map<BroadcastMetadata, File> map = new HashMap<BroadcastMetadata, File>();
        BroadcastMetadata broadcastMetadata = new BroadcastMetadata();
        XMLGregorianCalendar now = CalendarUtils.getCalendar();
        broadcastMetadata.setStartTime(now);
        XMLGregorianCalendar later = CalendarUtils.getCalendar();
        later.add(DatatypeFactory.newInstance().newDuration(1000));
        broadcastMetadata.setStopTime(later);
        map.put(broadcastMetadata, new File(
                Thread.currentThread().getContextClassLoader().getResource("testfiletypes/" + filename).getFile()));
        request.setFileMap(map);
        return request;
    }
}
