package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.db.Metadata;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class MetadataDAOTest {

    private static HibernateUtil util;

    @Before
    public void setUp() throws Exception {
        String hibernateConfig = "src/test/config/hibernate.in-memory_unittest.cfg.xml";
        util = HibernateUtil.getInstance(hibernateConfig);
    }

    @Test
    @Ignore
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
