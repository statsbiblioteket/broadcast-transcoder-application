package dk.statsbiblioteket.broadcasttranscoder.fetcher;

import dk.statsbiblioteket.broadcasttranscoder.fetcher.cli.FetcherContext;
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
        FetcherContext context = new FetcherContext();
        context.setBatchSize(100);
        context.setCollection("doms:RadioTV_Collection");
        context.setState("Published");
        context.setViewAngle("GUI");
        context.setDomsPassword("fedoraAdminPass");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsEndpoint("http://alhena:7480/centralWebservice-service/central/");
        RecordDescription record;
        record = new RecordDescription();
        record.setPid("uuid:d11c2f49-4e6f-47bd-b04f-7ee6293520ea");
        System.out.println(record.getPid());
        DomsTranscodingStructureFetcher thing = new DomsTranscodingStructureFetcher();
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
