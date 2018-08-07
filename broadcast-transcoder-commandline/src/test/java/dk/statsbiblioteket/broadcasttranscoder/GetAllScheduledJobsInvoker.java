package dk.statsbiblioteket.broadcasttranscoder;



//java -Dlogback.configurationFile=$confDir/logback-queryChangesDoms.xml $hibernate_log_config \

import java.io.File;
import java.net.URISyntaxException;

public class GetAllScheduledJobsInvoker {
    
    public static void main(String[] args) throws URISyntaxException {
        File infrastructure = new File(Thread.currentThread()
                                             .getContextClassLoader()
                                             .getResource("bta.infrastructure.iapetus.properties")
                                             .toURI());
        File behaivour = new File(Thread.currentThread()
                                             .getContextClassLoader()
                                             .getResource("bta.fetcher.Broadcast.properties")
                                             .toURI());
    
        File hibernate = new File(Thread.currentThread()
                                        .getContextClassLoader()
                                        .getResource("hibernate.iapetus.cfg.xml")
                                        .toURI());
    
        GetAllScheduledJobs.main(new String[]{
                "--infrastructure_configfile", infrastructure.getAbsolutePath(),
                "--behavioural_configfile",behaivour.getAbsolutePath(),
                "--hibernate_configfile",hibernate.getAbsolutePath(),
                "-timestamp", "0"});
    }
}
