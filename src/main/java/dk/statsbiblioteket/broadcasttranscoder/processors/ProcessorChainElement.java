package dk.statsbiblioteket.broadcasttranscoder.processors;

import java.util.Properties;

/**
 *
 */
public abstract class ProcessorChainElement {
    private ProcessorChainElement childElement;


    public void setChildElement(ProcessorChainElement childElement) {
        this.childElement = childElement;
    }

    public void processIteratively(TranscodeRequest request, Properties props) throws ProcessorException {

        processThis(request, props);
        if (childElement != null) childElement.processIteratively(request, props);
    }

    protected abstract void processThis(TranscodeRequest request, Properties props) throws ProcessorException;

}
