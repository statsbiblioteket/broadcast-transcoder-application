package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(ProcessorChainElement.class);
    private ProcessorChainElement childElement;

    public static ProcessorChainElement makeChain(ProcessorChainElement... elements) {

        ProcessorChainElement previous = null;
        for (ProcessorChainElement element : elements) {
            if (previous != null) {
                previous.setChildElement(element);
            }
            previous = element;
        }
        return elements[0];

    }


    public void setChildElement(ProcessorChainElement childElement) {
        this.childElement = childElement;
    }

    public void processIteratively(TranscodeRequest request, Context context) throws ProcessorException {
        logger.info("Processing with " + this.getClass() + " on " + context.getProgrampid());
        processThis(request, context);
        if (childElement != null) childElement.processIteratively(request, context);
    }

    protected abstract void processThis(TranscodeRequest request, Context context) throws ProcessorException;

    protected ProcessorChainElement() {
    }

    protected ProcessorChainElement(ProcessorChainElement childElement) {
        this.childElement = childElement;
    }
}
