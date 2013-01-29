package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import junit.framework.TestCase;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class ProgramMetadataFetcherProcessorTest {

    @Before
    public void setUp() {
        try {
            InetAddress.getByName("alhena");
        } catch (UnknownHostException e) {
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void testProcessThis() throws ProcessorException {
        ProgramMetadataFetcherProcessor processor = new ProgramMetadataFetcherProcessor();
        TranscodeRequest request = new TranscodeRequest();
        Context context = new Context();
        context.setProgrampid("uuid:006d9eb9-0e24-43b4-a364-8d07e6e7a19d");
        context.setDomsEndpoint("http://alhena:7480/centralWebservice-service/central/");
        context.setDomsUsername("fedoraReadOnlyAdmin");
        context.setDomsPassword("fedoraReadOnlyPass");
        processor.processThis(request,context);
    }

}
