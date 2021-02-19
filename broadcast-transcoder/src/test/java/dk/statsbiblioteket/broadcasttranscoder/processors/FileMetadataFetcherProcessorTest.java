package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.opentest4j.TestAbortedException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 11/22/12
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileMetadataFetcherProcessorTest {

    @BeforeEach
    public void setUp() throws TestAbortedException {
        try {
            InetAddress.getByName("carme");
        } catch (UnknownHostException e) {
            throw new TestAbortedException();
        }
    }

    @Test
    public void testProcessIteratively() throws Exception {
        FileMetadataFetcherProcessor processor = new FileMetadataFetcherProcessor();
        TranscodeRequest request = new TranscodeRequest();
        SingleTranscodingContext context = new SingleTranscodingContext();
        request.setObjectPid("uuid:3a1bdfce-497f-4bff-8804-22ac000cca83");
        context.setDomsEndpoint("http://carme:7880/centralWebservice-service/central/");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsPassword("spD68ZJl");
        processor.processIteratively(request,context);
    }
}
