package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 11/26/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class TranscodingTimestampRecordDAO extends GenericHibernateDAO<TranscodingTimestampRecord, String> implements TimestampPersister {

    public TranscodingTimestampRecordDAO(HibernateUtilIF util) {
        super(TranscodingTimestampRecord.class, util);
    }

    @Override
    public Long getTimestamp(String programpid) {
        TranscodingTimestampRecord record = read(programpid);
        if (record == null) {
            return null;
        } else {
            return record.getLastTranscodedTimestamp();
        }
    }

    @Override
    public void setTimestamp(String programpid, long timestamp) {
        TranscodingTimestampRecord record = read(programpid);
        if (record != null) {
            record.setLastTranscodedTimestamp(timestamp);
            update(record);
        } else {
            record = new TranscodingTimestampRecord();
            record.setDomsProgramPid(programpid);
            record.setLastTranscodedTimestamp(timestamp);
            create(record);
        }
    }
}
