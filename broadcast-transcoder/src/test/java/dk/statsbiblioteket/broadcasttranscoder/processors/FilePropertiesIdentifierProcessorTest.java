package dk.statsbiblioteket.broadcasttranscoder.processors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Test of file properties identifier.
 */
public class FilePropertiesIdentifierProcessorTest {
    @BeforeEach
    public void setUp() throws Exception {

    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testProcessThis() throws Exception {
        TranscodeRequest request;
        FilePropertiesIdentifierProcessor filePropertiesIdentifierProcessor = new FilePropertiesIdentifierProcessor();

        request = getMockRequest("file.ts");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals(FileFormatEnum.SINGLE_PROGRAM_AUDIO_TS, request.getFileFormat(), "Should have right file type");
        assertEquals(0, request.getBitrate(), "Should have right bit rate");

        request = getMockRequest("file.mpg");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals(FileFormatEnum.MPEG_PS, request.getFileFormat(), "Should have right file type");
        assertEquals(0, request.getBitrate(), "Should have right bit rate");

        request = getMockRequest("file.mpeg");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals(FileFormatEnum.MPEG_PS, request.getFileFormat(), "Should have right file type");
        assertEquals(0, request.getBitrate(), "Should have right bit rate");

        request = getMockRequest("file.mpeg1");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals(FileFormatEnum.MPEG_PS, request.getFileFormat(), "Should have right file type");
        assertEquals(0, request.getBitrate(), "Should have right bit rate");

        request = getMockRequest("file.wav");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals(FileFormatEnum.AUDIO_WAV, request.getFileFormat(), "Should have right file type");
        assertEquals(0, request.getBitrate(), "Should have right bit rate");

        request = getMockRequest("muxfile.ts");
        filePropertiesIdentifierProcessor.processThis(request, null);
        assertEquals(FileFormatEnum.MULTI_PROGRAM_MUX, request.getFileFormat(), "Should have right file type");
        assertEquals(0, request.getBitrate(), "Should have right bit rate");

        TranscodeRequest specialRequest = getMockRequest("file.txt");
        assertThrows(Exception.class, () -> {filePropertiesIdentifierProcessor.processThis(specialRequest, null);  }, "Should throw excpetion on unknown file type");
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
