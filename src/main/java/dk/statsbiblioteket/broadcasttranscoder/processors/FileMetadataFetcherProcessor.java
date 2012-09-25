package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramStructure;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.broadcasttranscoder.util.JaxbWrapper;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.Relation;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FileMetadataFetcherProcessor extends ProcessorChainElement {

    private static final String HAS_FILE_RELATION = "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile";
    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        List<String> fileObjectPids = null;
        try {
            fileObjectPids = findFileObjects(context);
        } catch (Exception e) {
           throw new ProcessorException(e);
        }
        List<BroadcastMetadata> broadcastMetadata = getBroadcastMetadata(fileObjectPids, context);
        request.setBroadcastMetadata(broadcastMetadata);
    }

    private List<BroadcastMetadata> getBroadcastMetadata(List<String> fileObjectPids, Context context) throws ProcessorException {
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        JaxbWrapper<BroadcastMetadata> broadcastMetadataWrapper = null;
        try {
            broadcastMetadataWrapper =
                    new JaxbWrapper<BroadcastMetadata>(getClass().getClassLoader().getResource("BROADCAST_METADATA.xsd")
                            ,BroadcastMetadata.class);
        } catch (Exception e) {
            throw new ProcessorException(e);
        }
        List<BroadcastMetadata> broadcastMetadataList = new ArrayList<BroadcastMetadata>();
        for (String fileObjectPid: fileObjectPids) {
            BroadcastMetadata broadcastMetadata = null;
            try {
                String broadcastMetadataXml = doms.getDatastreamContents(fileObjectPid, "BROADCAST_METADATA");
                broadcastMetadata = broadcastMetadataWrapper.xmlToObject(broadcastMetadataXml);
            } catch (Exception e) {
                throw new ProcessorException(e);
            }
            broadcastMetadataList.add(broadcastMetadata);
        }
        return broadcastMetadataList;
    }


    private List<String> findFileObjects(Context context) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        List<String> fileObjectPids = new ArrayList<String>();
        List<Relation> relations = doms.getRelations(context.getProgrampid());
        for (Relation relation: relations) {
            if (relation.getPredicate().equals(HAS_FILE_RELATION)) {
                  fileObjectPids.add(relation.getSubject());
            }
        }
        return fileObjectPids;
    }
}
