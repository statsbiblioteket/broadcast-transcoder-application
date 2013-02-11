package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This processor requests the PBCORE datastream from doms and extracts the title and whether or not we have TVMETER metadata
 */
public class PbcoreMetadataExtractorProcessor extends ProcessorChainElement {
    private static Logger logger = LoggerFactory.getLogger(PbcoreMetadataExtractorProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String structureXmlString = null;
        try {
            structureXmlString = domsAPI.getDatastreamContents(request.getObjectPid(), "PBCORE");
        } catch (Exception e) {
            throw new ProcessorException("Failed to get PBCORE for "+request.getObjectPid(),e);
        }
        request.setTvmeter(hasTvmeter(structureXmlString));
        request.setTitle(getTitle(structureXmlString));
        String type = getType(structureXmlString);
        request.setVideo("Moving Image".equals(type));
    }

    boolean hasTvmeter(String structureXmlString) {
        XPathSelector xpath = DOM.createXPathSelector("pb", "http://www.pbcore.org/PBCore/PBCoreNamespace.html");
        Document doc = DOM.stringToDOM(structureXmlString, true);
        String tvmeterMetadata = xpath.selectString(doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreIdentifier[pb:identifierSource='tvmeter']/pb:identifier");
        return ( (tvmeterMetadata != null) && (tvmeterMetadata.trim().length() > 100));
    }

    String getTitle(String structureXmlString) {
        XPathSelector xpath = DOM.createXPathSelector("pb", "http://www.pbcore.org/PBCore/PBCoreNamespace.html");
        Document doc = DOM.stringToDOM(structureXmlString, true);
        return xpath.selectString(doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreTitle[pb:titleType='titel']/pb:title");
    }

    String getType(String structureXmlString) {
        XPathSelector xpath = DOM.createXPathSelector("pb", "http://www.pbcore.org/PBCore/PBCoreNamespace.html");
        Document doc = DOM.stringToDOM(structureXmlString, true);
        return xpath.selectString(doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreInstantiation/pb:formatMediaType");
    }

}
