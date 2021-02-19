package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 */
public class BroadcastTranscoderApplicationTest{

    @Test
    public void testMainError() throws Exception {
        assertThrows(OptionParseException.class, () -> BroadcastTranscoderApplication.main(null), "expecting exception");
    }

    @Test
    public void testMainCorrect() throws Exception {
          BroadcastTranscoderApplication.main(new String[] {"-hibernate_configfile", "file3", "-infrastructure_configfile", "file2", "-u","-behavioural_configfile", "file1", "-programpid", "foobar"});
    }
}
