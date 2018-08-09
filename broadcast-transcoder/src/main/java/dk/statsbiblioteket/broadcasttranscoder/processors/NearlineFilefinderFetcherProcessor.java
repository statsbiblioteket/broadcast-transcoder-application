package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.NearlineFileFinder;

/**
 * This processor brings each file in the BroadcastMetadata online, and initialises the Filemap
 */
public class NearlineFilefinderFetcherProcessor extends ProcessorChainElement {

    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        request.setFileMap(new NearlineFileFinder().findAndBringOnline(request, context));
    }
}
