package dk.statsbiblioteket.broadcasttranscoder.fetcher;

import dk.statsbiblioteket.broadcasttranscoder.fetcher.cli.FetcherContext;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.RecordDescription;
import junit.framework.TestCase;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class BtaDomsFetcherTest {

    @Before
    public void setUp() {
        try {
            InetAddress.getByName("alhena");
        } catch (UnknownHostException e) {
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void testMain() throws Exception {
            BtaDomsFetcher.main(
                    new String[] {
                            "-infrastructure_configfile",
                            new File(Thread.currentThread().getContextClassLoader().getResource("bta.infrastructure.properties").toURI()).toString(),
                            "-behavioural_configfile",
                            new File(Thread.currentThread().getContextClassLoader().getResource("bta.fetcher.properties").toURI()).toString(),
                            "-since", "0"
                    });
    }

    @Test
    public void testFetcher() throws InvalidCredentialsException, MethodFailedException {
        FetcherContext context = new FetcherContext();
        context.setBatchSize(100);
        context.setCollection("doms:RadioTV_Collection");
        context.setState("Published");
        context.setViewAngle("SummaVisible");
        context.setDomsPassword("fedoraAdminPass");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsEndpoint("http://alhena:7880/centralWebservice-service/central/");
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        List<RecordDescription> records = BtaDomsFetcher.requestInBatches(doms, context);
        for (RecordDescription record : records) {
            System.out.println(record.getPid()+":"+record.getDate());
        }
        assertTrue("No records found",records.size()>0);
    }
}
