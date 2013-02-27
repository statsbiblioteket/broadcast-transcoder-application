package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets the "isGoForTranscoding" flag in the request on the basis of whether or not the output file
 * already exists and whether or not overwrite flag is set.
 */
public class GoNoGoProcessor extends ProcessorChainElement {
    private static Logger logger = LoggerFactory.getLogger(GoNoGoProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
         if (FileUtils.hasFinalMediaOutputFile(request, context) && !context.isOverwrite()) {
             logger.info("Transcoded file already found. No transcoding necessary.");
             request.setGoForTranscoding(false);
         } else {
             request.setGoForTranscoding(true);
         }
    }
}
