package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import junit.framework.TestCase;

/**
 *
 */
public class ProgramMetadataFetcherProcessorTest extends TestCase {

    public void testProcessThis() {
        ProgramMetadataFetcherProcessor processor = new ProgramMetadataFetcherProcessor();
        TranscodeRequest request = new TranscodeRequest();
        Context context = new Context();
        context.setProgrampid("");
        context.setDomsEndpoint("http://alhena:7880/centralWebservice-service/central/");
        context.setDomsUsername("fedoraReadOnlyAdmin");
        context.setDomsPassword("fedoraReadOnlyPass");
    }

}
