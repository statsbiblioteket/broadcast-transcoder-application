package dk.statsbiblioteket.broadcasttranscoder.persistence;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;


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

    @BeforeEach
    public void setUp() throws Exception {
        configFile = new File(Thread.currentThread().getContextClassLoader().getResource("hibernate.cfg.xml").toURI()).getAbsolutePath();

        dao = new BroadcastTranscodingRecordDAO(HibernateUtil.getInstance(configFile));
        dao.create(createRecord());
        dao.create(createRecord());
        dao.create(createRecord());
        BroadcastTranscodingRecord record = createRecord();
        known = record.getID();
        dao.create(record);
        dao.create(createRecord());

    }

    @AfterEach
    public void tearDown() throws Exception {
        List<BroadcastTranscodingRecord> transcodings = dao.getAllTranscodings(0, null);
        for (BroadcastTranscodingRecord transcoding : transcodings) {
            dao.delete(transcoding);
        }
    }

    public BroadcastTranscodingRecord createRecord(){
        BroadcastTranscodingRecord record1 = new BroadcastTranscodingRecord();
        record1.setID("doms:" + UUID.randomUUID().toString());
        record1.setTranscodingState(TranscodingStateEnum.PENDING);
        record1.setDomsLatestTimestamp(new Date().getTime()-1000);
        return record1;
    }


    @Test
    public void testGetAllTranscodings() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(5));
        Long cutoff = pendings.get(2).getDomsLatestTimestamp();
        List<BroadcastTranscodingRecord> fewerPendings = dao.getAllTranscodings(cutoff, TranscodingStateEnum.PENDING);
        assertThat(fewerPendings.size(), is(3));


    }

    @Test
    public void testMarkAsChangedInDoms() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(5));
        BroadcastTranscodingRecord latest = pendings.get(4);
        assertThat(latest.getID(), not(known));


        dao.markAsChangedInDoms(known,new Date().getTime());
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(5));
        latest = pendings.get(4);
        assertThat(latest.getID(),is(known));
    }

    @Test
    public void testMarkAsAlreadyTranscoded() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(5));

        dao.markAsAlreadyTranscoded(known,new Date().getTime());
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(4));
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.COMPLETE);
        assertThat(pendings.size(), is(1));

    }

    @Test
    public void testMarkAsFailed() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(5));

        dao.markAsFailed(known,new Date().getTime(),"Test of failed");
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(4));
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.FAILED);
        assertThat(pendings.size(), is(1));

    }

    @Test
    public void testMarkAsRejected() throws Exception {
        List<BroadcastTranscodingRecord> pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(5));

        dao.markAsRejected(known,new Date().getTime(),"Test of rejected");
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.PENDING);
        assertThat(pendings.size(), is(4));
        pendings = dao.getAllTranscodings(0, TranscodingStateEnum.REJECTED);
        assertThat(pendings.size(), is(1));

    }
}
