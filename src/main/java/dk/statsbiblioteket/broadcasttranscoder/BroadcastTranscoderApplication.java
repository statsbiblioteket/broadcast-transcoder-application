package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.processors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class BroadcastTranscoderApplication {

    private static Logger logger = LoggerFactory.getLogger(BroadcastTranscoderApplication.class);

    public static void main(String[] args) throws OptionParseException, ProcessorException {
        logger.debug("Entered main method.");
        Context context = new OptionsParser().parseOptions(args);
        TranscodeRequest request = new TranscodeRequest();
        ProcessorChainElement metadata = new FileMetadataFetcherProcessor();
        ProcessorChainElement filedata = new FileMetadataFetcherProcessor();
        ProcessorChainElement sorter = new BroadcastMetadataSorterProcessor();
        ProcessorChainElement fetcher = new FilefinderFetcherProcessor();
        ProcessorChainElement identifier = new FilePropertiesIdentifierProcessor();
        ProcessorChainElement clipper = new ClipMarshallerProcessor();
        ProcessorChainElement fixer = new StructureFixerProcessor();
        ProcessorChainElement dispatcher = new TranscoderDispatcherProcessor();
        metadata.setChildElement(filedata);
        filedata.setChildElement(sorter);
        sorter.setChildElement(fetcher);
        fetcher.setChildElement(identifier);
        identifier.setChildElement(clipper);
        clipper.setChildElement(fixer);
        fixer.setChildElement(dispatcher);
        metadata.processIteratively(request, context);
    }

}
