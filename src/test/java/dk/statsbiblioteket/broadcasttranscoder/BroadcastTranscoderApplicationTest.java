package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/24/12
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
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
          BroadcastTranscoderApplication.main(new String[] {"-domsendpoint", "http://some.thing"});
    }
}
