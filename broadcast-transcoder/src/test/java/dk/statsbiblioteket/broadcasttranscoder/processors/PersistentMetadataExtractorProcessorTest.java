package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import junit.framework.TestCase;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class PersistentMetadataExtractorProcessorTest {

    @Before
    public void setUp() {
        try {
            InetAddress.getByName("alhena");
        } catch (UnknownHostException e) {
            Assume.assumeNoException(e);
        }
    }

    @Test
    @Ignore
    public void testPersistentMetadataExtractor() throws ProcessorException, IOException {
        PersistentMetadataExtractorProcessor processor = new PersistentMetadataExtractorProcessor();
        Context context = new Context();
        context.setProgrampid("foobar");
        context.setDomsEndpoint("http://alhena:7880/centralWebservice-service/central/");
        context.setDomsPassword("fedoraReadOnlyPass");
        context.setDomsUsername("fedoraReadOnlyAdmin");
       processor.processThis(null, context);
    }

    @Test
    @Ignore
    public void testPersistentMetadataExtractorWithFetcher() throws ProcessorException {
        ProgramMetadataFetcherProcessor fetcher = new ProgramMetadataFetcherProcessor();
        PersistentMetadataExtractorProcessor processor = new PersistentMetadataExtractorProcessor();
        Context context = new Context();
        context.setProgrampid("uuid:d82107be-20cf-4524-b611-07d8534b97f8");
        context.setDomsEndpoint("http://carme:7880/centralWebservice-service/central/");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsPassword("spD68ZJl");
        TranscodeRequest request = new TranscodeRequest();
        fetcher.setChildElement(processor);
        fetcher.processIteratively(request, context);
    }

}
