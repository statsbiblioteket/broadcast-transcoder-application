package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.NearlineFileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class FilefinderProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FilefinderProcessor.class);


    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {
        if ( ! new NearlineFileFinder().isAllFilesOnline(request,context)){
            request.setGoForTranscoding(false);
            logger.warn("Will not transcode for "+request + " and "+context+" as not all files are online");
        }
    }
}
