package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import dk.statsbiblioteket.broadcasttranscoder.persistence.*;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.ReklamefilmTranscodingRecord;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class ReklamefilmTranscodingRecordDAO extends TranscodingRecordDao<ReklamefilmTranscodingRecord> {

    public ReklamefilmTranscodingRecordDAO(HibernateUtilIF util) {
        super(ReklamefilmTranscodingRecord.class, util);
    }

}
