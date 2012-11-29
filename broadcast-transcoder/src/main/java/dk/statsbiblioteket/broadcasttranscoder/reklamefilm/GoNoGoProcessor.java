package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;

/**
 * Sets the "isGoForTranscoding" flag in the request on the basis of whether or not the output file
 * already exists (and any other criteria we might think of later).
 */
public class GoNoGoProcessor extends ProcessorChainElement {
    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        //TODO implement
        throw new RuntimeException("not implemented");
    }
}
