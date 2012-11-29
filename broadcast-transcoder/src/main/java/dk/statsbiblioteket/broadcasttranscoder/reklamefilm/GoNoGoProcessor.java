package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets the "isGoForTranscoding" flag in the request on the basis of whether or not the output file
 * already exists (and any other criteria we might think of later).
 */
public class GoNoGoProcessor extends ProcessorChainElement {
    private static Logger logger = LoggerFactory.getLogger(GoNoGoProcessor.class);

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
         if (FileUtils.hasMediaOutputFile(request, context)) {
             logger.info("Transcoded file already found. No transcoding necessary.");
             request.setGoForTranscoding(false);
         } else {
             request.setGoForTranscoding(true);
         }
    }
}
