package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramStructure;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;


/**
 * This processor requests the existing PROGRAM_STRUCTURE and PROGRAM_BROADCAST and adds them to the request
 */
public class ProgramMetadataFetcherProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(ProgramMetadataFetcherProcessor.class);



    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        ProgramBroadcast programBroadcast = getProgramBroadcast(request,context);
        request.setProgramBroadcast(programBroadcast);
        ProgramStructure programStructure = getProgramStructure(request,context);
        request.setDomsProgramStructure(programStructure);

    }



    private ProgramStructure getProgramStructure(TranscodeRequest request,InfrastructureContext context) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String structureXmlString = null;
        try {
            try {
                structureXmlString = domsAPI.getDatastreamContents(request.getObjectPid(), "PROGRAM_STRUCTURE");
            } catch (InvalidResourceException e) {
               logger.info("No PROGRAM_STRUCTURE datastream in " + request.getObjectPid());
               return null;
            }
            logger.debug("Program Structure for " + request.getObjectPid() + "\n" + structureXmlString);
        } catch (Exception e) {
            throw new ProcessorException("Failed to get Program Structure for "+request.getObjectPid(),e);
        }
        ProgramStructure programStructure = null;
        try {
            //JaxbWrapper<ProgramStructure> programStructureWrapper = new JaxbWrapper<ProgramStructure>(getClass().getClassLoader().getResource("ProgramStructure.xsd"),ProgramStructure.class);
            //programStructure = programStructureWrapper.xmlToObject(structureXmlString);
            JAXBContext jaxbContext = JAXBContext.newInstance(ProgramStructure.class);
            programStructure = jaxbContext.createUnmarshaller().unmarshal(new StreamSource(new StringReader(structureXmlString)), ProgramStructure.class).getValue();
        } catch (Exception e) {
            throw new ProcessorException("Failed to unmarshal ProgramStructure for "+request.getObjectPid(),e);
         }
        return programStructure;
    }

    private ProgramBroadcast getProgramBroadcast(TranscodeRequest request,InfrastructureContext context) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String broadcastXmlString = null;
        try {
            broadcastXmlString = domsAPI.getDatastreamContents(request.getObjectPid(), "PROGRAM_BROADCAST");
            logger.debug("Broadcast Structure for " +request.getObjectPid() + "\n" + broadcastXmlString);
        } catch (Exception e) {
            throw new ProcessorException("Failed to get Broadcast Structure for " + request.getObjectPid(),e);
        }
        ProgramBroadcast programBroadcast = null;
        try {
            //final URL resource = getClass().getClassLoader().getResource("ProgramBroadCast.xsd");
            JAXBContext jaxbContext = JAXBContext.newInstance(ProgramBroadcast.class.getPackage().getName());
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            Object jaxbthing = unmarshaller.unmarshal(new StringReader(broadcastXmlString));
            programBroadcast = ((JAXBElement<ProgramBroadcast>)jaxbthing).getValue();

            programBroadcast = JAXBContext.newInstance(ProgramBroadcast.class).createUnmarshaller().unmarshal(new StreamSource(new StringReader(broadcastXmlString)), ProgramBroadcast.class).getValue();

            //JaxbWrapper<ProgramBroadcast> programBroadcastWrapper = new JaxbWrapper<ProgramBroadcast>(resource,ProgramBroadcast.class);
            //programBroadcast = programBroadcastWrapper.xmlToObject(broadcastXmlString);
        } catch (Exception e) {
            throw new ProcessorException("Fault barrier for "+request.getObjectPid(),e);
         }
        return programBroadcast;
    }

}
