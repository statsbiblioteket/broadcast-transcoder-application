package dk.statsbiblioteket.broadcasttranscoder.persistence;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class MetadataDAOTest {

    private static HibernateUtil util;

    @Before
    public void setUp() throws Exception {


    }

    @Test
    @Ignore
    public void testCreate() {
        String hibernateConfig = "src/test/config/hibernate.in-memory_unittest.cfg.xml";
        util = HibernateUtil.getInstance(hibernateConfig);

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
        /*assertEquals(2, mds.size());
        assertTrue(mds.get(0).getLastChangedDate().after(mds.get(1).getLastChangedDate()));*/
    }

}
