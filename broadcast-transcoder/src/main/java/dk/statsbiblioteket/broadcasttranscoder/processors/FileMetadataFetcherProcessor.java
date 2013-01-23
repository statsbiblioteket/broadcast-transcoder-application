package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class FileMetadataFetcherProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FileMetadataFetcherProcessor.class);

    public FileMetadataFetcherProcessor() {
    }

    public FileMetadataFetcherProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

    private static final String HAS_FILE_RELATION = "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile";
    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        List<String> fileObjectPids = null;
        try {
            fileObjectPids = findFileObjects(context);
        } catch (Exception e) {
           throw new ProcessorException("Failed to find file objects for "+context.getProgrampid(),e);
        }
        if (fileObjectPids.isEmpty()) {
            throw new ProcessorException("No file-object relations for program " + context.getProgrampid());
        }
        List<BroadcastMetadata> broadcastMetadata = getBroadcastMetadata(fileObjectPids, context, request);
        request.setBroadcastMetadata(broadcastMetadata);
    }

    private List<BroadcastMetadata> getBroadcastMetadata(List<String> fileObjectPids, SingleTranscodingContext context, TranscodeRequest request) throws ProcessorException {
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
                throw new ProcessorException("Failed to get Broadcast Metadata for "+context.getProgrampid(),e);
            }
            broadcastMetadataList.add(broadcastMetadata);
            pidMap.put(fileObjectPid, broadcastMetadata);
        }
        request.setPidMap(pidMap);
        return broadcastMetadataList;
    }


    private List<String> findFileObjects(SingleTranscodingContext context) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
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
