package dk.statsbiblioteket.broadcasttranscoder;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
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

    private File foobar4;

    @Before
    public void setUp() throws Exception {
        foobar4 = new File("./foobar4");
        foobar4.mkdir();
        foobar4.deleteOnExit();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(foobar4);


    }

    @Test
    public void testMain() throws Exception {


        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String infraProperties = new File(contextClassLoader.getResource("bta.infrastructure.properties").toURI()).getAbsolutePath();
        String hibernate = new File(contextClassLoader.getResource("hibernate-derby.xml").toURI()).getAbsolutePath();
        String programsList = new File(contextClassLoader.getResource("programAnalyzerProgramList.txt").toURI()).getAbsolutePath();
        String fileSizes = new File(contextClassLoader.getResource("fileSizeList.txt").toURI()).getAbsolutePath();
        String behaivor = new File(contextClassLoader.getResource("bta.behaviour.properties").toURI()).getAbsolutePath();
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
