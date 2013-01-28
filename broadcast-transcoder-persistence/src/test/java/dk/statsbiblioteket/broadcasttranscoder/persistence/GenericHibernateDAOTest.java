package dk.statsbiblioteket.broadcasttranscoder.persistence;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.GenericHibernateDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/24/13
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenericHibernateDAOTest {
    String configFile;
    @Before
    public void setUp() throws Exception {

        configFile = new File(Thread.currentThread().getContextClassLoader().getResource("hibernate.cfg.xml").toURI()).getAbsolutePath();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateReadUpdateDelete() throws Exception {

        GenericHibernateDAO<BroadcastTranscodingRecord, String> dao
                = new GenericHibernateDAO<BroadcastTranscodingRecord, String>(
                BroadcastTranscodingRecord.class,
                HibernateUtil.getInstance(configFile));
        BroadcastTranscodingRecord testObject = new BroadcastTranscodingRecord();

        testObject.setDomsPid("doms:test1");
        testObject.setTitle("titleSet");
        testObject.setDomsLatestTimestamp(new Date().getTime());


        dao.create(testObject);
        BroadcastTranscodingRecord readObject = dao.read(testObject.getDomsPid());
        assertThat(readObject,is(testObject));
        System.out.println(readObject);

        readObject.setTitle("title is not different");

        assertThat(readObject,not(testObject));

        dao.update(readObject);

        BroadcastTranscodingRecord readObject2 = dao.read(readObject.getDomsPid());

        assertThat(readObject2,is(readObject));

        dao.delete(readObject2);

        BroadcastTranscodingRecord readObject3 = dao.read(readObject2.getDomsPid());
        assertThat((readObject3),IsNull.nullValue());

    }

}
