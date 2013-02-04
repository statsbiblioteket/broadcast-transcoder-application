package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.FfprobeType;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 11/30/12
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class FfprobeFetcherProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FfprobeFetcherProcessor.class);
    private static final String HAS_FILE_RELATION = "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasFile";


    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        CentralWebservice domsApi = CentralWebserviceFactory.getServiceInstance(context);
        String fileObjectPid = null;
        List<Relation> relations = null;
        try {
            relations = domsApi.getRelations(request.getObjectPid());
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
        for (Relation relation: relations) {
            if (relation.getPredicate().equals(HAS_FILE_RELATION)) {
                fileObjectPid = relation.getObject();
            }
        }
        FfprobeType ffprobe = null;
        try {
            String broadcastMetadataXml = domsApi.getDatastreamContents(fileObjectPid, "FFPROBE");
            logger.debug("Found file metadata '" + fileObjectPid + "' :\n" + broadcastMetadataXml);
            ffprobe = JAXBContext.newInstance(FfprobeType.class).createUnmarshaller().unmarshal(new StreamSource(new StringReader(broadcastMetadataXml)), FfprobeType.class).getValue();
        } catch (Exception e) {
            throw new ProcessorException("Failed to get Broadcast Metadata for "+request.getObjectPid(),e);
        }
        Float duration = ffprobe.getFormat().getDuration();
        request.setFfprobeDurationSeconds(duration);
        request.setSnapshotPaddingSeconds(1);
    }
}
