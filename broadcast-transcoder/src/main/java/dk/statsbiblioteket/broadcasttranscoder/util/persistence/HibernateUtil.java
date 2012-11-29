package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import java.io.File;

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

    private HibernateUtil() {

    }

    public static synchronized HibernateUtil getInstance(String configFilePath) {
        if (instance == null) {
            instance = new HibernateUtil();
            File file = new File(configFilePath);
            AnnotationConfiguration configure = (new AnnotationConfiguration()).configure(file);
            configure.addAnnotatedClass(BroadcastTranscodingRecord.class);
            configure.addAnnotatedClass(ReklamefileTranscodingRecord.class);
            factory = configure.buildSessionFactory();
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
