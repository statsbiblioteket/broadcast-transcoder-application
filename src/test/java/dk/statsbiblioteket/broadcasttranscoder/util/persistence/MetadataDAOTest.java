package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.db.Metadata;
import junit.framework.TestCase;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class MetadataDAOTest extends TestCase {

    private static HibernateUtil util;

    @Override
    protected void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        String hibernateConfig = "src/test/config/hibernate.in-memory_unittest.cfg.xml";
        util = HibernateUtil.getInstance(hibernateConfig);
    }

    public void testCreate() {
        Metadata md = new Metadata();
        md.setChannelID("dr1");
        md.setProgramUuid("foobar");
        md.setLastChangedDate(new Date());
        MetadataDAO dao = new MetadataDAO(util);
        dao.create(md);
        md = new Metadata();
        md.setChannelID("dr1");
        md.setProgramUuid("foobar");
        long now = new Date().getTime();
        md.setLastChangedDate(new Date(now + 10000L));
        dao.create(md);
        List<Metadata> mds = dao.getByProgramPid("foobar");
        assertEquals(2, mds.size());
        assertTrue(mds.get(0).getLastChangedDate().after(mds.get(1).getLastChangedDate()));
    }

}
