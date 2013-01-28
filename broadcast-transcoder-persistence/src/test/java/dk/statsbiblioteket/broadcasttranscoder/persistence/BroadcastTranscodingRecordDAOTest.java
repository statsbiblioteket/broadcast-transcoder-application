package dk.statsbiblioteket.broadcasttranscoder.persistence;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/24/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class BroadcastTranscodingRecordDAOTest {
    private String configFile;
    BroadcastTranscodingRecordDAO dao;
    private String known;

    @Before
    public void setUp() throws Exception {
        configFile = new File(Thread.currentThread().getContextClassLoader().getResource("hibernate.cfg.xml").toURI()).getAbsolutePath();

        dao = new BroadcastTranscodingRecordDAO(HibernateUtil.getInstance(configFile));
        dao.create(createRecord());
        dao.create(createRecord());
        dao.create(createRecord());
        BroadcastTranscodingRecord record = createRecord();
        known = record.getDomsPid();
        dao.create(record);
        dao.create(createRecord());

    }

    @After
    public void tearDown() throws Exception {
        List<BroadcastTranscodingRecord> transcodings = dao.getAllTranscodings(0, null);
        for (BroadcastTranscodingRecord transcoding : transcodings) {
            dao.delete(transcoding);
        }
    }

    public BroadcastTranscodingRecord createRecord(){
        BroadcastTranscodingRecord record1 = new BroadcastTranscodingRecord();
        record1.setDomsPid("doms:" + UUID.randomUUID().toString());
        record1.setTranscodingState(TranscodingStateEnum.PENDING);
        record1.setDomsLatestTimestamp(new Date().getTime()-1000);
        return record1;
    }


    @Test
    public void testGetAllTranscodings() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(5));
        Long cutoff = pendings.get(2).getDomsLatestTimestamp();
        List<BroadcastTranscodingRecord> fewerPendings = dao.getAllTranscodings(cutoff, TranscodingStateEnum.PENDING);
        assertThat(fewerPendings.size(),Is.is(3));


    }

    @Test
    public void testMarkAsChangedInDoms() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(5));
        BroadcastTranscodingRecord latest = pendings.get(4);
        assertThat(latest.getDomsPid(), IsNot.not(known));


        dao.markAsChangedInDoms(known,new Date().getTime());
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(5));
        latest = pendings.get(4);
        assertThat(latest.getDomsPid(),Is.is(known));
    }

    @Test
    public void testMarkAsAlreadyTranscoded() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(5));

        dao.markAsAlreadyTranscoded(known);
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(4));
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.COMPLETE);
        assertThat(pendings.size(), Is.is(1));

    }

    @Test
    public void testMarkAsFailed() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(5));

        dao.markAsFailed(known,"Test of failed");
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(4));
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.FAILED);
        assertThat(pendings.size(), Is.is(1));

    }

    @Test
    public void testMarkAsRejected() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(5));

        dao.markAsRejected(known,"Test of rejected");
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), Is.is(4));
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.REJECTED);
        assertThat(pendings.size(), Is.is(1));

    }
}
