package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public interface HibernateUtilIF {

    public SessionFactory getSessionFactory();

    public Session getSession();

}
