package dk.statsbiblioteket.broadcasttranscoder.ws.config;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.parsers.SingleTranscodingOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;

import java.io.IOException;

/**
 *
 */
public class WebserviceTranscodingOptionsParser extends SingleTranscodingOptionsParser<BroadcastTranscodingRecord> {

    public static SingleTranscodingContext<BroadcastTranscodingRecord> readProperties(SingleTranscodingContext<BroadcastTranscodingRecord> transcodingContext)
            throws IOException, OptionParseException {
        readBehaviouralProperties(transcodingContext);
        readInfrastructureProperties(transcodingContext);
        return transcodingContext;
    }

}
