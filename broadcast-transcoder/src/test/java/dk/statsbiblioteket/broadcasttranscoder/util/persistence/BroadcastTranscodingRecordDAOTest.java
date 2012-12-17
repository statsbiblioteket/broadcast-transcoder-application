package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import junit.framework.TestCase;

import java.io.File;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 12/17/12
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class BroadcastTranscodingRecordDAOTest extends TestCase {

    public void testSetTimestamp() throws Exception {
        Context context = new Context();

        context.setHibernateConfigFile(new File(Thread.currentThread().getContextClassLoader().getResource("hibernate-derby.xml").toURI()));
        HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
        context.setTimestampPersister(new BroadcastTranscodingRecordDAO(util));
        TimestampPersister persister = context.getTimestampPersister();
        long timestamp = new Date().getTime();
        persister.setTimestamp("uuid:testid1",timestamp);

        Long retrieved = persister.getTimestamp("uuid:testid1");
        assertTrue("timestamps not equal after save",timestamp ==retrieved );

    }
}
