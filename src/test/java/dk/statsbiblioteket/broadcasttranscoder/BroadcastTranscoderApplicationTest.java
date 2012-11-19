package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import junit.framework.TestCase;

/**
 *
 */
public class BroadcastTranscoderApplicationTest extends TestCase {

    public void testMainError() throws Exception {
        try {
            BroadcastTranscoderApplication.main(null);
            fail();
        } catch (OptionParseException e) {
            //expected
        }
    }

    public void testMainCorrect() throws OptionParseException, ProcessorException {
          BroadcastTranscoderApplication.main(new String[] {"-hibernate_configfile", "file3", "-infrastructure_configfile", "file2", "-u","-behavioural_configfile", "file1", "-programpid", "foobar"});
    }
}
