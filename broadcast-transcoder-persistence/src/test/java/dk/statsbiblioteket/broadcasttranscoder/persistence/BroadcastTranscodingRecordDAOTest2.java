package dk.statsbiblioteket.broadcasttranscoder.persistence;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 12/17/12
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */

public class BroadcastTranscodingRecordDAOTest2  {

    HibernateUtil util;
    private String programPid;

    @Before
    public void setUp() throws Exception {

        util = HibernateUtil.getInstance(getHibernateConfigFile().getAbsolutePath());
        util.getSession().clear();

        programPid = "uuid:test1";
    }

    private File getHibernateConfigFile() throws URISyntaxException {
        return new File(Thread.currentThread().getContextClassLoader().getResource("hibernate.cfg.xml").toURI());
    }



    @Test
    public void testSomthing(){



        BroadcastTranscodingRecordDAO dao = new BroadcastTranscodingRecordDAO(util);
        Long temp = dao.read(programPid).getLastTranscodedTimestamp();
        assertNull("Database is not empty",temp);



        dao.read(programPid).setLastTranscodedTimestamp(new Date().getTime());
        BroadcastTranscodingRecord record = dao.read(programPid);



        record.setTvmeter(false);

        record.setBroadtcastStartTime(new Date(new Date().getTime()-10000));
        record.setBroadcastEndTime(new Date(new Date().getTime()));
        record.setChannel("DR1");
        record.setEndOffset(300);
        record.setStartOffset(600);
        record.setTitle("TestRecord");
        dao.update(record);

        BroadcastTranscodingRecord record2 = dao.read(programPid);

        System.out.println(record);
        System.out.println(record2);
        assertEquals("object changed by persistence",record,record2);


    }
}
