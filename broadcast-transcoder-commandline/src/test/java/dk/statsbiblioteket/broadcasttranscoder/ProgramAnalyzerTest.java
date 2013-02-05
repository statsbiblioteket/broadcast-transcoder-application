package dk.statsbiblioteket.broadcasttranscoder;

import org.junit.Test;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/5/13
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProgramAnalyzerTest {
    @Test
    public void testMain() throws Exception {


        String infraProperties = new File(Thread.currentThread().getContextClassLoader().getResource("bta.infrastructure.properties").toURI()).getAbsolutePath();
        String hibernate = new File(Thread.currentThread().getContextClassLoader().getResource("hibernate-derby.xml").toURI()).getAbsolutePath();
        String programsList = new File(Thread.currentThread().getContextClassLoader().getResource("programAnalyzerProgramList.txt").toURI()).getAbsolutePath();
        String fileSizes = new File(Thread.currentThread().getContextClassLoader().getResource("fileSizeList.txt").toURI()).getAbsolutePath();
        String behaivor = new File(Thread.currentThread().getContextClassLoader().getResource("bta.behaviour.properties").toURI()).getAbsolutePath();
        String[] args = new String[]{
                "--infrastructure_configfile="+infraProperties
                ,"--hibernate_configfile="+hibernate
                ,"--behavioural_configfile="+behaivor
                ,"--programList="+programsList
                ,"--fileSizes="+fileSizes
        };

        ProgramAnalyzer.main(args);
    }
}
