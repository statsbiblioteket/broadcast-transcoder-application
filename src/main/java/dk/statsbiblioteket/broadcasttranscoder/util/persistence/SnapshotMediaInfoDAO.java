/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.db.SnapshotMediaInfo;

public class SnapshotMediaInfoDAO extends GenericHibernateDAO<SnapshotMediaInfo, Long> {

    public SnapshotMediaInfoDAO(HibernateUtilIF util) {
        super(SnapshotMediaInfo.class, util);
    }
}
