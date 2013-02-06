package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 *
 */
public class BroadcastTranscoderApplicationTest{

    @Test
    public void testMainError() throws Exception {
        try {
            BroadcastTranscoderApplication.main(null);
            fail();
        } catch (OptionParseException e) {
            //expected
        }
    }

    @Test
    public void testMainCorrect() throws Exception {
          BroadcastTranscoderApplication.main(new String[] {"-hibernate_configfile", "file3", "-infrastructure_configfile", "file2", "-u","-behavioural_configfile", "file1", "-programpid", "foobar"});
    }
}
