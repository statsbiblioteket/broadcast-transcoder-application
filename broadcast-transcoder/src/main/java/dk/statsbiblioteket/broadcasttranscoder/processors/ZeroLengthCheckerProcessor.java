package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 12/4/12
 * Time: 9:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ZeroLengthCheckerProcessor extends ProcessorChainElement {

    private static final Logger logger = LoggerFactory.getLogger(ZeroLengthCheckerProcessor.class);


    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        File outputFile = FileUtils.getFinalMediaOutputFile(request, context);
        if (outputFile == null) {
            final String message = "null output file found for " + request.getObjectPid();
            logger.error(message);
            throw new ProcessorException(message);
        } else if (outputFile.length() == 0) {
            final String message = "Zero length output file: " + outputFile.getAbsolutePath() + ". Deleting.";
            logger.error(message);
            outputFile.delete();
            throw new ProcessorException(message);
        }
    }
}
