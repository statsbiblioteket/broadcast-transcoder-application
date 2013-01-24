package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.GetJobsContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.GetJobsOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.BroadcastTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.TranscodingProcessInterface;
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
        GetJobsContext context = null;
        try {
            context = new GetJobsOptionsParser().parseOptions(args);
            HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
            TranscodingProcessInterface dao = new BroadcastTranscodingRecordDAO(util);
            List<BroadcastTranscodingRecord> jobs = dao.getAllTranscodings(context.getFromTimestamp(), context.getState());
            for (BroadcastTranscodingRecord job : jobs) {
                System.out.println(job.getDomsProgramPid()
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
