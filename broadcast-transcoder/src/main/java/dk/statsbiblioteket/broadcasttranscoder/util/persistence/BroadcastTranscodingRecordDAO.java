package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

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
}
