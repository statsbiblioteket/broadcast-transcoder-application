package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.HibernateSessionFactoryFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/28/12
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class HibernateUtil implements HibernateUtilIF {

   private static SessionFactory factory;

    private static HibernateUtil instance;

    private HibernateUtil(String configFilePath) {
          factory = HibernateSessionFactoryFactory.create(configFilePath);
    }

    public static synchronized HibernateUtil getInstance(String configFilePath) {
        if (instance == null) {
            instance = new HibernateUtil(configFilePath);
        }
        return instance;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return factory;
    }

    @Override
    public Session getSession() {
        return factory.openSession();
    }
}
