package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.extraction.model.PBCoreProgramMetadata;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 12/4/12
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class PbcoreMetadataExtractorProcessor extends ProcessorChainElement {
    private static Logger logger = LoggerFactory.getLogger(PbcoreMetadataExtractorProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String structureXmlString = null;
        try {
            structureXmlString = domsAPI.getDatastreamContents(context.getProgrampid(), "PBCORE");
        } catch (Exception e) {
            throw new ProcessorException("Failed to get PBCORE for "+context.getProgrampid(),e);
        }
        request.setTvmeter(hasTvmeter(structureXmlString));
        request.setTitle(getTitle(structureXmlString));
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
}
