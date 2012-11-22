package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramStructure;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.broadcasttranscoder.util.JaxbWrapper;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.net.URL;


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
        request.setDomsProgramStructure(programStructure);

    }



    private ProgramStructure getProgramStructure(Context context) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String structureXmlString = null;
        try {
            try {
                structureXmlString = domsAPI.getDatastreamContents(context.getProgrampid(), "PROGRAM_STRUCTURE");
            } catch (InvalidResourceException e) {
               logger.info("No PROGRAM_STRUCTURE datastream in " + context.getProgrampid());
               return null;
            }
            logger.debug("Program Structure for " + context.getProgrampid() + "\n" + structureXmlString);
        } catch (Exception e) {
            throw new ProcessorException(e);
        }
        ProgramStructure programStructure = null;
        try {
            //JaxbWrapper<ProgramStructure> programStructureWrapper = new JaxbWrapper<ProgramStructure>(getClass().getClassLoader().getResource("ProgramStructure.xsd"),ProgramStructure.class);
            //programStructure = programStructureWrapper.xmlToObject(structureXmlString);
            JAXBContext jaxbContext = JAXBContext.newInstance(ProgramStructure.class);
            programStructure = jaxbContext.createUnmarshaller().unmarshal(new StreamSource(new StringReader(structureXmlString)), ProgramStructure.class).getValue();
        } catch (Exception e) {
            throw new ProcessorException(e);
         }
        return programStructure;
    }

    private ProgramBroadcast getProgramBroadcast(Context context) throws ProcessorException {
        CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
        String broadcastXmlString = null;
        try {
            broadcastXmlString = domsAPI.getDatastreamContents(context.getProgrampid(), "PROGRAM_BROADCAST");
            logger.debug("Broadcast Structure for " + context.getProgrampid() + "\n" + broadcastXmlString);
        } catch (Exception e) {
            throw new ProcessorException(e);
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
            throw new ProcessorException(e);
         }
        return programBroadcast;
    }

}
