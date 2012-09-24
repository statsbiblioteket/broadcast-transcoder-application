package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.BroadcastTranscoderApplication;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


/**
 *
 */
public class OptionsParser {

    protected static final Option DOMS_ENDPOINT_OPTION = new Option("domsendpoint", true, "The service location for the DOMS endpoint");

    protected static Options options;

    private Context context;

    public OptionsParser() {
        context = new Context();
        options = new Options();
        options.addOption(DOMS_ENDPOINT_OPTION);
    }


    public Context parseOptions(String[] args) throws OptionParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            parseError(e.toString());
            throw new OptionParseException(e.getMessage(), e);
        }
        parseDOMSEndpoint(cmd);
        return context;
    }

    protected void parseDOMSEndpoint(CommandLine cmd) throws OptionParseException {
        String domsEndpoint = cmd.getOptionValue(DOMS_ENDPOINT_OPTION.getOpt());
        if (domsEndpoint == null) {
            parseError(DOMS_ENDPOINT_OPTION.toString());
            throw new OptionParseException(DOMS_ENDPOINT_OPTION.toString());
        } else {
            context.setDomsEndpoint(domsEndpoint);
        }
    }

    protected void printUsage() {
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printHelp(BroadcastTranscoderApplication.class.getName(), options, true);
    }

    protected void parseError(String message) {
        System.err.println("Error parsing arguments");
        System.err.println(message);
        printUsage();
    }

}
