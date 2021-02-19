package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.FetcherContext;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.RecordDescription;
import dk.statsbiblioteket.doms.central.ViewBundle;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.opentest4j.TestAbortedException;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class BtaDomsFetcherTest {

    private File foobar4;

    @BeforeEach
      public void setUp() throws Exception {
          foobar4 = new File("./foobar4");
          foobar4.mkdir();
          foobar4.deleteOnExit();
        try {
            InetAddress.getByName("alhena");
        } catch (UnknownHostException e) {
            throw new TestAbortedException();
        }

      }

      @AfterEach
      public void tearDown() throws Exception {
          FileUtils.deleteDirectory(foobar4);


      }



    @Test
    public void testMain() throws Exception {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource("bta.infrastructure.properties");
        final File infrastructureFile = new File(resource.toURI());
        URL resource1 = contextClassLoader.getResource("bta.fetcher.properties");
        final File behaviouralFile = new File(resource1.toURI());
        String hibernate = new File(contextClassLoader.getResource("hibernate-derby.xml").toURI()).getAbsolutePath();

        assertTrue(infrastructureFile.exists(), infrastructureFile.getAbsolutePath() + " should exist.");
        assertTrue(behaviouralFile.exists(), behaviouralFile.getAbsolutePath() + " should exist.");
        BtaDomsFetcher.main(
                new String[]{
                        "-infrastructure_configfile",
                        infrastructureFile.getAbsolutePath(),
                        "-behavioural_configfile",
                        behaviouralFile.getAbsolutePath(),
                        "-hibernate_configfile",hibernate,
                        "-since", "0"
                });
    }


    @Test
    @Disabled("Too slow")
    public void testFetcher() throws InvalidCredentialsException, MethodFailedException {
        FetcherContext context = new FetcherContext();
        context.setBatchSize(100);
        context.setCollection("doms:RadioTV_Collection");
        context.setFedoraState("Published");
        context.setViewAngle("SummaVisible");
        context.setDomsPassword("fedoraAdminPass");
        context.setDomsUsername("fedoraAdmin");
        context.setDomsEndpoint("http://alhena:7980/centralWebservice-service/central/");
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        List<RecordDescription> records = BtaDomsFetcher.requestInBatches(doms, context);
        for (RecordDescription record : records) {
            System.out.println(record.getPid()+":"+record.getDate());
        }
        assertTrue(records.size()>0, "No records found");
    }

}
