package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.NearlineFileFinder;

/**
 *
 */
public class FilefinderFetcherProcessor extends ProcessorChainElement {
    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        request.setFileMap(NearlineFileFinder.findAndBringOnline(request.getBroadcastMetadata()));
    }
}
