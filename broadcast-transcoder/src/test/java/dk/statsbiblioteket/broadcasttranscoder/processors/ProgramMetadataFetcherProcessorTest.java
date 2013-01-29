package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import junit.framework.TestCase;

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
        SingleTranscodingContext context = new SingleTranscodingContext();
        context.setProgrampid("uuid:01248937-fc19-4dc5-b701-d92fec52d3d0");
        context.setDomsEndpoint("http://alhena:7880/centralWebservice-service/central/");
        context.setDomsUsername("fedoraReadOnlyAdmin");
        context.setDomsPassword("fedoraReadOnlyPass");
        processor.processThis(request,context);
    }

}
