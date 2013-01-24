package dk.statsbiblioteket.broadcasttranscoder.persistence;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.MetadataDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.Metadata;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
    public void testCreate() throws URISyntaxException {
        String configFile = new File(Thread.currentThread().getContextClassLoader().getResource("hibernate.cfg.xml").toURI()).getAbsolutePath();


        util = HibernateUtil.getInstance(configFile);

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
