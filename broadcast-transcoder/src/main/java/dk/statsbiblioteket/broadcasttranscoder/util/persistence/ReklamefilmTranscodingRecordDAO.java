package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

/**
 *
 */
public class ReklamefilmTranscodingRecordDAO extends GenericHibernateDAO<ReklamefileTranscodingRecord, String> implements TimestampPersister {

    //TODO implement the methods

    public ReklamefilmTranscodingRecordDAO(HibernateUtilIF util) {
        super(ReklamefileTranscodingRecord.class, util);
    }

    @Override
    public Long getTimestamp(String programpid) {
        throw new RuntimeException("not implemented") ;
    }

    @Override
    public void setTimestamp(String programpid, long timestamp) {
        throw new RuntimeException("not implemented");
    }
}
