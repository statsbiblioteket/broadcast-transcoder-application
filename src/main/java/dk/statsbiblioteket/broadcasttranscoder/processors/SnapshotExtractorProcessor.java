package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 10/1/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SnapshotExtractorProcessor extends ProcessorChainElement {

    private static final int scale = 26; //multiple by 16x9 for final size



    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
