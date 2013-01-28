package dk.statsbiblioteket.broadcasttranscoder.persistence.dao;

import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.Metadata;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/28/12
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetadataDAO extends GenericHibernateDAO<Metadata, Long> {

    public MetadataDAO(HibernateUtilIF util) {
        super(Metadata.class, util);
    }

   public List<Metadata> getByProgramPid(String pid) {
       return (List<Metadata>)getSession().createCriteria(Metadata.class).add(Restrictions.eq("programUuid", pid)).addOrder(Order.desc("lastChangedDate")).list();
   }

}
