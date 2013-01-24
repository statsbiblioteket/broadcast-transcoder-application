package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.*;
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
    private static String configFilePath;

    private HibernateUtil() {

    }

    public static synchronized HibernateUtil getInstance(String configFilePath) {
        HibernateUtil.configFilePath = configFilePath;
        if (instance == null) {
            instance = new HibernateUtil();
            File file = new File(configFilePath);
            AnnotationConfiguration configure = (new AnnotationConfiguration()).configure(file);
            configure.addAnnotatedClass(BroadcastTranscodingRecord.class);
            configure.addAnnotatedClass(ReklamefilmTranscodingRecord.class);
            configure.addAnnotatedClass(Metadata.class);
            configure.addAnnotatedClass(PreviewMediaInfo.class);
            configure.addAnnotatedClass(ProgramMediaInfo.class);
            configure.addAnnotatedClass(SnapshotMediaInfo.class);
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

    public void reload(){
        instance = null;
        instance = getInstance(configFilePath);
    }
}
