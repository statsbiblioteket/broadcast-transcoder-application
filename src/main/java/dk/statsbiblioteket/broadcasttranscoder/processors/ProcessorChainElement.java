package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 *
 */
public abstract class ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(ProcessorChainElement.class);
    private ProcessorChainElement childElement;


    public void setChildElement(ProcessorChainElement childElement) {
        this.childElement = childElement;
    }

    public void processIteratively(TranscodeRequest request, Context context) throws ProcessorException {
        logger.info("Processing with " + this.getClass() + " on " + context.getProgrampid());
        processThis(request, context);
        if (childElement != null) childElement.processIteratively(request, context);
    }

    protected abstract void processThis(TranscodeRequest request, Context context) throws ProcessorException;

}
