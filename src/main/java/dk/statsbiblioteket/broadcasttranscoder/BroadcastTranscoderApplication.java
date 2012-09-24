package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionsParser;

/**
 *
 */
public class BroadcastTranscoderApplication {

    public static void main(String[] args) throws OptionParseException {
         Context context = new OptionsParser().parseOptions(args);
    }

}
