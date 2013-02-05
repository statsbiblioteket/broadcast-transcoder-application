package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.util.NearlineFileFinder;

/**
 * This processor brings each file in the BroadcastMetadata online, and initialises the Filemap
 */
public class FilefinderFetcherProcessor extends ProcessorChainElement {

    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        request.setFileMap(new NearlineFileFinder().findAndBringOnline(request, context));
    }
}
