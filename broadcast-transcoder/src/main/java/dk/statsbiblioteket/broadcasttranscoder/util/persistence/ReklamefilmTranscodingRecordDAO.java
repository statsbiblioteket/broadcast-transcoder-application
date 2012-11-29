package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import java.util.Date;

/**
 *
 */
public class ReklamefilmTranscodingRecordDAO extends GenericHibernateDAO<ReklamefilmTranscodingRecord, String> implements TimestampPersister {

    public ReklamefilmTranscodingRecordDAO(HibernateUtilIF util) {
        super(ReklamefilmTranscodingRecord.class, util);
    }

    @Override
    public Long getTimestamp(String programpid) {
        return read(programpid).getTranscodingTimestamp();
    }

    @Override
    public void setTimestamp(String programpid, long timestamp) {
        ReklamefilmTranscodingRecord record = read(programpid);
        if (record == null) {
            record = new ReklamefilmTranscodingRecord();
            record.setDomsPid(programpid);
            record.setTranscodingTimestamp(timestamp);
            record.setTranscodingDate(new Date(timestamp));
            create(record);
        } else {
            record.setTranscodingTimestamp(timestamp);
            record.setTranscodingDate(new Date(timestamp));
            update(record);
        }
    }
}
