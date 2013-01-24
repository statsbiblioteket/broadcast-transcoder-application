package dk.statsbiblioteket.broadcasttranscoder.persistence;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class ReklamefilmTranscodingRecordDAO extends GenericHibernateDAO<ReklamefilmTranscodingRecord, String> implements TranscodingProcessInterface<ReklamefilmTranscodingRecord> {

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

    @Override
    public List<BroadcastTranscodingRecord> getAllTranscodings(long since, TranscodingState state) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markAsChangedInDoms(String programpid, long timestamp) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markAsAlreadyTranscoded(String programpid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markAsFailed(String programpid, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markAsRejected(String programpid, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
