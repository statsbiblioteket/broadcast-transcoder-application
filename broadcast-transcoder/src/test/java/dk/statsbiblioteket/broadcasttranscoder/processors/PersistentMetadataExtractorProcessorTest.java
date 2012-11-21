package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 11/21/12
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class PersistentMetadataExtractorProcessorTest extends TestCase {

    public void testPersistentMetadataExtractor() throws ProcessorException {
        PersistentMetadataExtractorProcessor processor = new PersistentMetadataExtractorProcessor();
        Context context = new Context();
        context.setProgrampid("foobar");
        context.setDomsEndpoint("http://alhena:7880/centralWebservice-service/central/");
        context.setDomsPassword("fedoraReadOnlyPass");
        context.setDomsUsername("fedoraReadOnlyAdmin");
       processor.processThis(null, context);
    }
}
