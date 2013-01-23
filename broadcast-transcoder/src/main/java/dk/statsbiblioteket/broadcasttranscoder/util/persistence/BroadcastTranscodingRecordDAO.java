package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 11/26/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class BroadcastTranscodingRecordDAO extends GenericHibernateDAO<BroadcastTranscodingRecord, String> implements TimestampPersister {

    public BroadcastTranscodingRecordDAO(HibernateUtilIF util) {
        super(BroadcastTranscodingRecord.class, util);
    }

    @Override
    public Long getTimestamp(String programpid) {
        BroadcastTranscodingRecord record = read(programpid);
        if (record == null) {
            return null;
        } else {
            return record.getLastTranscodedTimestamp();
        }
    }

    @Override
    public void setTimestamp(String programpid, long timestamp) {
        BroadcastTranscodingRecord record = read(programpid);
        if (record != null) {
            record.setLastTranscodedTimestamp(timestamp);
            update(record);
        } else {
            record = new BroadcastTranscodingRecord();
            record.setDomsProgramPid(programpid);
            record.setLastTranscodedTimestamp(timestamp);
            create(record);
        }
    }

    public List<BroadcastTranscodingRecord> getAllTranscodings(long since, TranscodingState state){
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        List jobs = session.createCriteria(BroadcastTranscodingRecord.class)
                .add(Restrictions.ge("domsLatestTimestamp", since))
                .add(Restrictions.eq("transcodingState", state))
                .list();
        tx.commit();
        session.close();
        return jobs;
    }

    public void markAsChangedInDoms(String programpid,long timestamp){
        BroadcastTranscodingRecord record = read(programpid);
        record.setDomsLatestTimestamp(timestamp);
        record.setTranscodingState(TranscodingState.PENDING);
        update(record);
    }

    public void markAsAlreadyTranscoded(String programpid){
        BroadcastTranscodingRecord record = read(programpid);
        record.setTranscodingState(TranscodingState.COMPLETE);
        record.setLastTranscodedTimestamp(record.getDomsLatestTimestamp());
        update(record);
    }

    public void markAsFailed(String programpid,String message){
        BroadcastTranscodingRecord record = read(programpid);
        record.setTranscodingState(TranscodingState.FAILED);
        record.setFailureMessage(message);
        update(record);
    }

    public void markAsRejected(String programpid,String message){
        BroadcastTranscodingRecord record = read(programpid);
        record.setTranscodingState(TranscodingState.REJECTED);
        record.setFailureMessage(message);
        update(record);
    }

}
