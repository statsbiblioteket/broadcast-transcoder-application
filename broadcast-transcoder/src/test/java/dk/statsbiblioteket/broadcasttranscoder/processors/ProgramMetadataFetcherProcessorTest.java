package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import junit.framework.TestCase;

/**
 *
 */
public class ProgramMetadataFetcherProcessorTest extends TestCase {

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
