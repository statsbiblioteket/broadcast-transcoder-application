package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.domscontent.PBCoreDescriptionDocument;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;

/**
 *
 */
public class JaxbWrapperTest extends TestCase {
    public void testXmlToObject() throws Exception {
        JaxbWrapper<PBCoreDescriptionDocument> wrapper = new JaxbWrapper<PBCoreDescriptionDocument>(getClass().getClassLoader().getResource("pbcore.xsd"), PBCoreDescriptionDocument.class);
        File pbcoreFile = new File("src/test/resources/pbcore1.xml");
        PBCoreDescriptionDocument doc = wrapper.xmlToObject(new FileInputStream(pbcoreFile));
    }
}
