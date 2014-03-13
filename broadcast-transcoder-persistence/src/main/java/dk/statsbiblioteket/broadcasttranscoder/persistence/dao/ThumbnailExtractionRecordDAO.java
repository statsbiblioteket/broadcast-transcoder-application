package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.ThumbnailExtractionRecord;

/**
 *
 */
public class ThumbnailExtractionRecordDAO extends GenericHibernateDAO<ThumbnailExtractionRecord, String> {

    public ThumbnailExtractionRecordDAO(HibernateUtilIF util) {
        super(ThumbnailExtractionRecord.class, util);
    }
}
