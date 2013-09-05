package dk.statsbiblioteket.broadcasttranscoder.ws;

import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URLEncoder;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/5/13
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class BroadcastTranscoderServiceTest {


    @Test
    public void testSanitiseTitle() throws Exception {
        String test1 = "foobar";
        assertEquals(test1, BroadcastTranscoderService.sanitiseTitle(test1));
        String test2 = "æ+fo;&o.b a_r\t\n%-'";
        assertEquals("æ+fo__o.b_a_r__%-_", BroadcastTranscoderService.sanitiseTitle(test2));
        String testEncoded = URLEncoder.encode(test2, "UTF-8");
        assertEquals(testEncoded, BroadcastTranscoderService.sanitiseTitle(testEncoded));
    }

    /**
     * Tests that the sanitised strings can actually be used as filenames in external bash processes with the
     * standard tools we have.
     * @throws ProcessorException
     * @throws ExternalProcessTimedOutException
     */
    @Test
    public void testSanitisedTitleInFilename() throws ProcessorException, ExternalProcessTimedOutException {
        String test2 = "æ+fo;&o.b a_r\t\n%-'";
        String filename = BroadcastTranscoderService.sanitiseTitle(test2) + ".txt";
        ExternalJobRunner.runClipperCommand("touch " + filename);
        File file = new File(filename);
        assertTrue(file.exists());
        ExternalJobRunner.runClipperCommand("rm " + filename);
        assertFalse(file.exists());
    }
}
