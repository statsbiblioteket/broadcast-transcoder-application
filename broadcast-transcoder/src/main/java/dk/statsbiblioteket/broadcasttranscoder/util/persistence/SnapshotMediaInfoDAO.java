/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

public class SnapshotMediaInfoDAO extends GenericHibernateDAO<SnapshotMediaInfo, Long> {

    public SnapshotMediaInfoDAO(HibernateUtilIF util) {
        super(SnapshotMediaInfo.class, util);
    }
}
