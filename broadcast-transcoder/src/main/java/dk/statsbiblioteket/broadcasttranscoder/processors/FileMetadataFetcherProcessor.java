package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.broadcasttranscoder.util.JaxbWrapper;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class FileMetadataFetcherProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FileMetadataFetcherProcessor.class);


    private static final String HAS_FILE_RELATION = "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile";
    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        List<String> fileObjectPids = null;
        try {
            fileObjectPids = findFileObjects(context);
        } catch (Exception e) {
           throw new ProcessorException(e);
        }
        List<BroadcastMetadata> broadcastMetadata = getBroadcastMetadata(fileObjectPids, context, request);
        request.setBroadcastMetadata(broadcastMetadata);
    }

    private List<BroadcastMetadata> getBroadcastMetadata(List<String> fileObjectPids, Context context, TranscodeRequest request) throws ProcessorException {
        Map<String, BroadcastMetadata> pidMap = new HashMap<String, BroadcastMetadata>();
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        List<BroadcastMetadata> broadcastMetadataList = new ArrayList<BroadcastMetadata>();
        for (String fileObjectPid: fileObjectPids) {
            BroadcastMetadata broadcastMetadata = null;
            try {
                String broadcastMetadataXml = doms.getDatastreamContents(fileObjectPid, "BROADCAST_METADATA");
                logger.debug("Found file metadata '" + fileObjectPid + "' :\n" + broadcastMetadataXml);
                broadcastMetadata = JAXBContext.newInstance(BroadcastMetadata.class).createUnmarshaller().unmarshal(new StreamSource(new StringReader(broadcastMetadataXml)), BroadcastMetadata.class).getValue();
            } catch (Exception e) {
                throw new ProcessorException(e);
            }
            broadcastMetadataList.add(broadcastMetadata);
            pidMap.put(fileObjectPid, broadcastMetadata);
        }
        request.setPidMap(pidMap);
        return broadcastMetadataList;
    }


    private List<String> findFileObjects(Context context) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        List<String> fileObjectPids = new ArrayList<String>();
        List<Relation> relations = doms.getRelations(context.getProgrampid());
        for (Relation relation: relations) {
            if (relation.getPredicate().equals(HAS_FILE_RELATION)) {
                  fileObjectPids.add(relation.getObject());
            }
        }
        return fileObjectPids;
    }
}
