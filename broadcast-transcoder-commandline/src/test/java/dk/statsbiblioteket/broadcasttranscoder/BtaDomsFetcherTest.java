package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.contexts.FetcherContext;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.RecordDescription;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class BtaDomsFetcherTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();


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
        exit.expectSystemExitWithStatus(0);
        final File infrastructureFile = new File(Thread.currentThread().getContextClassLoader().getResource("bta.infrastructure.properties").toURI());
        final File behaviouralFile = new File(Thread.currentThread().getContextClassLoader().getResource("bta.fetcher.properties").toURI());
        assertTrue(infrastructureFile.getAbsolutePath() + " should exist.", infrastructureFile.exists());
        assertTrue(behaviouralFile.getAbsolutePath() + " should exist.", behaviouralFile.exists());
        BtaDomsFetcher.main(
                new String[]{
                        "-infrastructure_configfile",
                        infrastructureFile.toString(),
                        "-behavioural_configfile",
                        behaviouralFile.toString(),
                        "-since", "0"
                });
    }

    @Test
    public void testFetcher() throws InvalidCredentialsException, MethodFailedException {
        FetcherContext context = new FetcherContext();
        context.setBatchSize(100);
        context.setCollection("doms:RadioTV_Collection");
        context.setFedoraState("Published");
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
