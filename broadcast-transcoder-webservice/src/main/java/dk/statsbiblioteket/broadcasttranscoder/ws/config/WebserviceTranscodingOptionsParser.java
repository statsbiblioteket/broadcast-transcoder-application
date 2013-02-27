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

    public WebserviceTranscodingOptionsParser(SingleTranscodingContext<BroadcastTranscodingRecord> transcodingContext)
            throws IOException, OptionParseException {
        this.readBehaviouralProperties(transcodingContext);
        this.readInfrastructureProperties(transcodingContext);
    }

}
