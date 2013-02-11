package dk.statsbiblioteket.broadcasttranscoder;

import org.junit.Test;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/11/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarkAsCompleteTest {
    @Test
    public void testMain() throws Exception {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String infraProperties = new File(contextClassLoader.getResource("bta.infrastructure.properties").toURI()).getAbsolutePath();
        String hibernate = new File(contextClassLoader.getResource("hibernate-derby.xml").toURI()).getAbsolutePath();
        String programsList = new File(contextClassLoader.getResource("markAsCompleteProgramList.txt").toURI()).getAbsolutePath();

        String[] args = new String[]{
                "--infrastructure_configfile="+infraProperties
                ,"--hibernate_configfile="+hibernate
                ,"--programList="+programsList
        };

        MarkAsComplete.main(args);

    }
}
