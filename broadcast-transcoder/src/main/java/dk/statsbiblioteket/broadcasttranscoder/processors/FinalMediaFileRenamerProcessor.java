package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 */
public class FinalMediaFileRenamerProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FinalMediaFileRenamerProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        final File tempFile = FileUtils.getTemporaryMediaOutputFile(request, context);
        final File finalFile = FileUtils.getFinalMediaOutputFile(request, context);
        logger.debug("Renaming " + tempFile.getAbsolutePath() + " to " + finalFile.getAbsolutePath());
        if (!tempFile.renameTo(finalFile)) {
            final String s = "Failed to rename " + tempFile.getAbsolutePath() + " to " + finalFile.getAbsolutePath();
            logger.error(s);
            throw new ProcessorException(s);
        }
    }
}
