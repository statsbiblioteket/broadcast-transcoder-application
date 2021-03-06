package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
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
 * This processor extracts the file pids from DOMS and a adds the BROADCAST_METADATA datastreams to the request-
 */
public class FileMetadataFetcherProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FileMetadataFetcherProcessor.class);

    private static final String HAS_FILE_RELATION = "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile";
    private static final String HAS_EXACT_FILE_RELATION = "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasExactFile";


    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {
        List<String> fileObjectPids = null;
        try {
            fileObjectPids = findFileObjects(request,context);
        } catch (Exception e) {
           throw new ProcessorException("Failed to find file objects for "+request.getObjectPid(),e);
        }
        if (fileObjectPids.isEmpty()) {
            throw new ProcessorException("No file-object relations for program " + request.getObjectPid());
        }
/*        if (request.isHasExactFile()) {
            String uniqueFilePid = fileObjectPids.get(0);
            CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
            BroadcastMetadata broadcastMetadata = new BroadcastMetadata();
            try {
                String filename = doms.getObjectProfile(uniqueFilePid).getTitle();
                broadcastMetadata.setFilename(filename);
            } catch (Exception e) {
                throw new ProcessorException("" + e);
            }
            Map<String, BroadcastMetadata> pidMap = new HashMap<String, BroadcastMetadata>();
            pidMap.put(uniqueFilePid, broadcastMetadata);
            request.setPidMap(pidMap);
            List<BroadcastMetadata> broadcastMetadatas = new ArrayList<BroadcastMetadata>();
            broadcastMetadatas.add(broadcastMetadata);
            request.setBroadcastMetadata(broadcastMetadatas);
        } else {*/
        //
        //
        //
            List<BroadcastMetadata> broadcastMetadata = getBroadcastMetadata(fileObjectPids, context, request);
            request.setBroadcastMetadata(broadcastMetadata);
       // }
    }

    private List<BroadcastMetadata> getBroadcastMetadata(List<String> fileObjectPids, InfrastructureContext context, TranscodeRequest request) throws ProcessorException {
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
                throw new ProcessorException("Failed to get Broadcast Metadata for "+request.getObjectPid(),e);
            }
            broadcastMetadataList.add(broadcastMetadata);
            pidMap.put(fileObjectPid, broadcastMetadata);
        }
        request.setPidMap(pidMap);
        return broadcastMetadataList;
    }

    /**
     * Returns either a list of all the files connected to this program or the single file that represents this
     * program exactly, if such a file exists.
     * @param request
     * @param context
     * @return
     * @throws InvalidCredentialsException
     * @throws InvalidResourceException
     * @throws MethodFailedException
     */
    private List<String> findFileObjects(TranscodeRequest request,InfrastructureContext context) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);
        List<String> fileObjectPids = new ArrayList<String>();
        List<Relation> relations = doms.getRelations(request.getObjectPid());
        for (Relation relation: relations) {
            logger.debug("Relation: " + request.getObjectPid() + " " + relation.getPredicate() + " " + relation.getObject());
            if (relation.getPredicate().equals(HAS_EXACT_FILE_RELATION)) {
                fileObjectPids = new ArrayList<String>();
                fileObjectPids.add(relation.getObject());
                request.setHasExactFile(true);
                logger.debug("Program " + request.getObjectPid() + " has an exact file " + relation.getObject());
                return fileObjectPids;
            } else if (relation.getPredicate().equals(HAS_FILE_RELATION)) {
                fileObjectPids.add(relation.getObject());
            }
        }
        return fileObjectPids;
    }
}
