package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/26/12
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExistingTranscodingFileProcessor extends ProcessorChainElement{

    public ExistingTranscodingFileProcessor() {
    }

    public ExistingTranscodingFileProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

    @Override
    /**
     * acquire the timestamp of the existing transcoding, or -1 if no existing transcoding exists
     */
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        //TODO
        context.setTimestampOfExistingTranscoding(0);
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
