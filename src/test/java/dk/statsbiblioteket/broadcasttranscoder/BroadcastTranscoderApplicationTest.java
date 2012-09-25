package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
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

    public void testMainCorrect() throws OptionParseException {
          BroadcastTranscoderApplication.main(new String[] {"-domsendpoint", "http://some.thing", "-programpid", "foobar"});
    }
}
