package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.DomsAndOverwriteExaminerProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.util.Streams;
import junit.framework.TestCase;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomsTranscodingStructureFetcherTest  {


    @Test
    public void testProcessThis() throws Exception {

        SingleTranscodingContext<TranscodingRecord> context = new SingleTranscodingContext<TranscodingRecord>();
        try {
            InetAddress.getByName("alhena");
        } catch (UnknownHostException e) {
            Assume.assumeNoException(e);
        }
        context.setDomsViewAngle("GUI");
        context.setDomsPassword("fedoraAdminPass");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsEndpoint("http://alhena:7480/centralWebservice-service/central/");
        TranscodeRequest request = new TranscodeRequest();
        request.setObjectPid("uuid:a3d19569-07c9-480f-8561-6dbf5e11d144");
        DomsAndOverwriteExaminerProcessor thing = new DomsAndOverwriteExaminerProcessor();
        thing.processThis(request,context);
        //TODO finish this test
    }

    @Test
    @Ignore
    public void testKillNewVersions() throws Exception {
        DomsAndOverwriteExaminerProcessor thing = new DomsAndOverwriteExaminerProcessor();
        String bundleString = Streams.getUTF8Resource("src/main/resources/xslt/sampleObject.xml");

        String result = thing.killNewerVersions(bundleString, 14000000000002L);

        XMLUnit.setIgnoreWhitespace(true);
        Diff smallDiff = new Diff(bundleString,result);
        assertTrue("pieces of XML are similar", smallDiff.similar());

    }

}
