package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramStructure;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.broadcasttranscoder.util.JaxbWrapper;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 *
 */
public class ProgramMetadataFetcherProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(ProgramMetadataFetcherProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        ProgramBroadcast programBroadcast = getProgramBroadcast(context);
        request.setProgramBroadcast(programBroadcast);
        ProgramStructure programStructure = getProgramStructure(context);
        request.setProgramStructure(programStructure);
    }

    private ProgramStructure getProgramStructure(Context context) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String structureXmlString = null;
        try {
            structureXmlString = domsAPI.getDatastreamContents(context.getProgrampid(), "PROGRAM_STRUCTURE_SCHEMA");
            logger.debug("Program Structure for " + context.getProgrampid() + "\n" + structureXmlString);
        } catch (Exception e) {
            throw new ProcessorException(e);
        }
        ProgramStructure programStructure = null;
        try {
            JaxbWrapper<ProgramStructure> programStructureWrapper = new JaxbWrapper<ProgramStructure>(getClass().getClassLoader().getResource("PROGRAM_STRUCTURE_SCHEMA.xsd"),ProgramStructure.class);
            programStructure = programStructureWrapper.xmlToObject(structureXmlString);
        } catch (Exception e) {
            throw new ProcessorException(e);
         }
        return programStructure;
    }

    private ProgramBroadcast getProgramBroadcast(Context context) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String broadcastXmlString = null;
        try {
            broadcastXmlString = domsAPI.getDatastreamContents(context.getProgrampid(), "PROGRAM_BROADCAST_SCHEMA");
            logger.debug("Broadcast Structure for " + context.getProgrampid() + "\n" + broadcastXmlString);
        } catch (Exception e) {
            throw new ProcessorException(e);
        }
        ProgramBroadcast programBroadcast = null;
        try {
            JaxbWrapper<ProgramBroadcast> programBroadcastWrapper = new JaxbWrapper<ProgramBroadcast>(getClass().getClassLoader().getResource("PROGRAM_BROADCAST_SCHEMA.xsd"),ProgramBroadcast.class);
            programBroadcast = programBroadcastWrapper.xmlToObject(broadcastXmlString);
        } catch (Exception e) {
            throw new ProcessorException(e);
         }
        return programBroadcast;
    }

}
