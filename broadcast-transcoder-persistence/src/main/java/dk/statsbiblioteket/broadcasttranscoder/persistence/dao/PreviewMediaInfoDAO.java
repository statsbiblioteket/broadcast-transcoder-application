package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.PreviewMediaInfo;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 10/1/12
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreviewMediaInfoDAO extends GenericHibernateDAO<PreviewMediaInfo, Long> {
    public PreviewMediaInfoDAO(HibernateUtilIF util) {
        super(PreviewMediaInfo.class, util);
    }
}
