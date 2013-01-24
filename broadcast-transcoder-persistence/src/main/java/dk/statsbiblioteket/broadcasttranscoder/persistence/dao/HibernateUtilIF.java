package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public interface HibernateUtilIF {

    public SessionFactory getSessionFactory();

    public Session getSession();

}
