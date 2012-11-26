package dk.statsbiblioteket.broadcasttranscoder.fetcher;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.fetcher.cli.FetcherContext;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.doms.central.RecordDescription;
import dk.statsbiblioteket.util.Streams;
import dk.statsbiblioteket.util.xml.DOM;
import junit.framework.TestCase;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomsTranscodingStructureFetcherTest extends TestCase {




    public void testProcessThis() throws Exception {
        Context context = new Context();
        context.setDomsViewAngle("GUI");
        context.setDomsPassword("fedoraAdminPass");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsEndpoint("http://alhena:7480/centralWebservice-service/central/");
        context.setProgrampid("uuid:a3d19569-07c9-480f-8561-6dbf5e11d144");
        DomsTranscodingStructureFetcher thing = new DomsTranscodingStructureFetcher();
        thing.processThis(new TranscodeRequest(),context);
        //TODO finish this test
    }


    public void testKillNewVersions() throws Exception {
        DomsTranscodingStructureFetcher thing = new DomsTranscodingStructureFetcher();
        String bundleString = Streams.getUTF8Resource("xslt/sampleObject.xml");

        String result = thing.killNewerVersions(bundleString, 14000000000002L);

        XMLUnit.setIgnoreWhitespace(true);
        Diff smallDiff = new Diff(bundleString,result);
        assertTrue("pieces of XML are similar", smallDiff.similar());

    }

}
