package dk.statsbiblioteket.broadcasttranscoder.fetcher;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.processors.DomsAndOverwriteExaminerProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.util.Streams;
import junit.framework.TestCase;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomsTranscodingStructureFetcherTest extends TestCase {




    public void testProcessThis() throws Exception {
        SingleTranscodingContext context = new SingleTranscodingContext();
        context.setDomsViewAngle("GUI");
        context.setDomsPassword("fedoraAdminPass");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsEndpoint("http://alhena:7480/centralWebservice-service/central/");
        context.setProgrampid("uuid:a3d19569-07c9-480f-8561-6dbf5e11d144");
        DomsAndOverwriteExaminerProcessor thing = new DomsAndOverwriteExaminerProcessor();
        thing.processThis(new TranscodeRequest(),context);
        //TODO finish this test
    }


    public void testKillNewVersions() throws Exception {
        DomsAndOverwriteExaminerProcessor thing = new DomsAndOverwriteExaminerProcessor();
        String bundleString = Streams.getUTF8Resource("src/main/resources/xslt/sampleObject.xml");

        String result = thing.killNewerVersions(bundleString, 14000000000002L);

        XMLUnit.setIgnoreWhitespace(true);
        Diff smallDiff = new Diff(bundleString,result);
        assertTrue("pieces of XML are similar", smallDiff.similar());

    }

}
