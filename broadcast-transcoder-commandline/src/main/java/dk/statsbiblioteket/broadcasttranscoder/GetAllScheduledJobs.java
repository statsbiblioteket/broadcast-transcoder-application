package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.GetJobsContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.parsers.GetJobsOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.ReklamefilmTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.TranscodingProcessInterface;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.ReklamefilmTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/23/13
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetAllScheduledJobs {
    private static Logger logger = LoggerFactory.getLogger(GetAllScheduledJobs.class);


    public static void main(String[] args){
        logger.debug("Entered main method.");
        GetJobsContext<TranscodingRecord> context = null;
        try {

            context = new GetJobsOptionsParser<TranscodingRecord>().parseOptions(args);
            HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
            List<? extends TranscodingRecord> jobs;
            if (context.getCollection().equals("doms:RadioTV_Collection")){
                TranscodingProcessInterface<BroadcastTranscodingRecord> dao = new BroadcastTranscodingRecordDAO(util);
                jobs = dao.getAllTranscodings(context.getFromTimestamp(), context.getState());
            } else if (context.getCollection().equals("doms:Collection_Reklamefilm")){
                TranscodingProcessInterface<ReklamefilmTranscodingRecord> dao = new ReklamefilmTranscodingRecordDAO(util);
                jobs = dao.getAllTranscodings(context.getFromTimestamp(), context.getState());
            } else {
                logger.error("Error in initial environment");
                System.exit(6);
                return;
            }
            for (TranscodingRecord job : jobs) {
                System.out.println(job.getID()
                        +" "+job.getDomsLatestTimestamp()
                        +" "+job.getTranscodingState()
                        +" "+job.getFailureMessage());
            }


        } catch (Exception e) {
            logger.error("Error in initial environment", e);
            System.exit(5);
        }
    }

}

