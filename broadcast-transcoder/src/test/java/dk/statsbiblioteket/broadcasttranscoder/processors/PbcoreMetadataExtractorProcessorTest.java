package dk.statsbiblioteket.broadcasttranscoder.processors;

import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 12/4/12
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class PbcoreMetadataExtractorProcessorTest extends TestCase {

    String test1 = "" +
            "<PBCoreDescriptionDocument xmlns=\"http://www.pbcore.org/PBCore/PBCoreNamespace.html\">" +
            "  <pbcoreIdentifier>" +
            "    <identifier>00941201101724461201101741141110319044212210110028001410100010004000400019970000Drengen de kaldte kylling                                   Drengen de kaldte kylling                                   Drengen de kaldte kylling                                       2          010000000000000000000Stereo    4:3       171000000000008021769700          000001TvmeterProgram</identifier>" +
            "    <identifierSource>tvmeter</identifierSource>" +
            "  </pbcoreIdentifier>" +
            "  <pbcoreIdentifier>" +
            "    <identifier>3904158RitzauProgram</identifier>" +
            "    <identifierSource>id</identifierSource>" +
            "  </pbcoreIdentifier>" +
            "  <pbcoreTitle>" +
            "    <title>Drengen de kaldte kylling</title>" +
            "    <titleType>titel</titleType>" +
            "  </pbcoreTitle>" +
            "  <pbcoreTitle>" +
            "    <title></title>" +
            "    <titleType>originaltitel</titleType>" +
            "  </pbcoreTitle>" +
            "  <pbcoreTitle>" +
            "    <title></title>" +
            "    <titleType>episodetitel</titleType>" +
            "  </pbcoreTitle>" +
            "</PBCoreDescriptionDocument>" ;

     String test2 = "" +
            "<PBCoreDescriptionDocument xmlns=\"http://www.pbcore.org/PBCore/PBCoreNamespace.html\">" +
            "  <pbcoreIdentifier>" +
            "    <identifier>TvmeterProgram</identifier>" +
            "    <identifierSource>tvmeter</identifierSource>" +
            "  </pbcoreIdentifier>" +
            "  <pbcoreIdentifier>" +
            "    <identifier>3904158RitzauProgram</identifier>" +
            "    <identifierSource>id</identifierSource>" +
            "  </pbcoreIdentifier>" +
            "  <pbcoreTitle>" +
            "    <title>Drengen de kaldte kylling</title>" +
            "    <titleType>titel</titleType>" +
            "  </pbcoreTitle>" +
            "  <pbcoreTitle>" +
            "    <title></title>" +
            "    <titleType>originaltitel</titleType>" +
            "  </pbcoreTitle>" +
            "  <pbcoreTitle>" +
            "    <title></title>" +
            "    <titleType>episodetitel</titleType>" +
            "  </pbcoreTitle>" +
            "</PBCoreDescriptionDocument>" ;

     String test3 = "" +
            "<PBCoreDescriptionDocument xmlns=\"http://www.pbcore.org/PBCore/PBCoreNamespace.html\">" +
            "  <pbcoreIdentifier>" +
            "    <identifier>3904158RitzauProgram</identifier>" +
            "    <identifierSource>id</identifierSource>" +
            "  </pbcoreIdentifier>" +
            "  <pbcoreTitle>" +
            "    <title>Drengen de kaldte kylling</title>" +
            "    <titleType>titel</titleType>" +
            "  </pbcoreTitle>" +
            "  <pbcoreTitle>" +
            "    <title></title>" +
            "    <titleType>originaltitel</titleType>" +
            "  </pbcoreTitle>" +
            "  <pbcoreTitle>" +
            "    <title></title>" +
            "    <titleType>episodetitel</titleType>" +
            "  </pbcoreTitle>" +
            "</PBCoreDescriptionDocument>" ;


    public void testIsTvmeterMetadata() {
        assertTrue((new PbcoreMetadataExtractorProcessor()).hasTvmeter(test1));
    }

     public void testIsTvmeterMetadataFalse() {
        assertFalse((new PbcoreMetadataExtractorProcessor()).hasTvmeter(test2));
        assertFalse((new PbcoreMetadataExtractorProcessor()).hasTvmeter(test3));
    }

}
