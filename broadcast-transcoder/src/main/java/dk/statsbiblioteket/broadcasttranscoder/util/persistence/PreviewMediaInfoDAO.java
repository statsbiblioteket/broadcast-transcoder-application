package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.db.PreviewMediaInfo;

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
