package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import dk.statsbiblioteket.broadcasttranscoder.persistence.*;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 11/26/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class BroadcastTranscodingRecordDAO extends TranscodingRecordDao<BroadcastTranscodingRecord> {

    public BroadcastTranscodingRecordDAO(HibernateUtilIF util) {
        super(BroadcastTranscodingRecord.class, util);
    }
}
