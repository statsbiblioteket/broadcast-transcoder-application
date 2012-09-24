package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;

import java.util.Properties;

/**
 *
 */
public abstract class ProcessorChainElement {
    private ProcessorChainElement childElement;


    public void setChildElement(ProcessorChainElement childElement) {
        this.childElement = childElement;
    }

    public void processIteratively(TranscodeRequest request, Context context) throws ProcessorException {

        processThis(request, context);
        if (childElement != null) childElement.processIteratively(request, context);
    }

    protected abstract void processThis(TranscodeRequest request, Context context) throws ProcessorException;

}
