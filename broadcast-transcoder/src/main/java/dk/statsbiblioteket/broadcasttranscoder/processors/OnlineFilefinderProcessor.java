package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.NearlineFileFinder;
import dk.statsbiblioteket.broadcasttranscoder.util.OnlineFileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 3/19/13
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class OnlineFilefinderProcessor extends ProcessorChainElement {
        private static Logger logger = LoggerFactory.getLogger(OnlineFilefinderProcessor.class);

    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        request.setFileMap(new OnlineFileFinder().findAndBringOnline(request, context));
        if (request.getFileMap().size() != request.getBroadcastMetadata().size()) {
            logger.info("Transcoding rejected because not all files are online");
            request.setRejected(true);
            this.setChildElement(null);
        }
    }
}
