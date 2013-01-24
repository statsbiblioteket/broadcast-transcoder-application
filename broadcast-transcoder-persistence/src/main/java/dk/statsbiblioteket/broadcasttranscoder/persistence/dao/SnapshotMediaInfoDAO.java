/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.SnapshotMediaInfo;

public class SnapshotMediaInfoDAO extends GenericHibernateDAO<SnapshotMediaInfo, Long> {

    public SnapshotMediaInfoDAO(HibernateUtilIF util) {
        super(SnapshotMediaInfo.class, util);
    }
}
