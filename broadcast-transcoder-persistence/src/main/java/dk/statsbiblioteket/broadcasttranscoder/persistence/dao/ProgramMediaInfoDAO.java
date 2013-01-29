package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.ProgramMediaInfo;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 10/1/12
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgramMediaInfoDAO extends GenericHibernateDAO<ProgramMediaInfo, Long> {

    public ProgramMediaInfoDAO(HibernateUtilIF util) {
        super(ProgramMediaInfo.class, util);
    }
}
