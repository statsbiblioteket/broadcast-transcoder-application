package dk.statsbiblioteket.broadcasttranscoder.persistence;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.GenericHibernateDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/24/13
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenericHibernateDAOTest {
    String configFile;
    @BeforeEach
    public void setUp() throws Exception {

        configFile = new File(Thread.currentThread().getContextClassLoader().getResource("hibernate.cfg.xml").toURI()).getAbsolutePath();
    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateReadUpdateDelete() throws Exception {
        GenericHibernateDAO<BroadcastTranscodingRecord, String> dao
                = new GenericHibernateDAO<BroadcastTranscodingRecord, String>(
                BroadcastTranscodingRecord.class,
                HibernateUtil.getInstance(configFile));
        BroadcastTranscodingRecord testObject = new BroadcastTranscodingRecord();

        testObject.setID("doms:test1");
        testObject.setTitle("titleSet");
        testObject.setDomsLatestTimestamp(new Date().getTime());


        dao.create(testObject);
        BroadcastTranscodingRecord readObject = dao.read(testObject.getID());
        assertEquals(readObject,testObject);
        System.out.println(readObject);

        readObject.setTitle("title is not different");

        assertNotEquals(readObject,testObject);

        dao.update(readObject);

        BroadcastTranscodingRecord readObject2 = dao.read(readObject.getID());

        assertEquals(readObject2,readObject);

        dao.delete(readObject2);

        BroadcastTranscodingRecord readObject3 = dao.read(readObject2.getID());
        assertNull(readObject3);

    }

}
