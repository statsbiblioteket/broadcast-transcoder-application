package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.BroadcastTranscoderApplication;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.*;

/**
 *
 */
public class OptionsParser {

    protected static final Option PID_OPTION = new Option("programpid", true, "The DOMS pid of the program to be transcoded");
    protected static final Option CONFIG_FILE_OPTION = new Option("configfile", true, "The config file");
    protected static Options options;

    private Context context;

    public OptionsParser() {
        context = new Context();
        options = new Options();
        options.addOption(PID_OPTION);
        options.addOption(CONFIG_FILE_OPTION);
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
        parseProgramPid(cmd);
        parseConfigFileOption(cmd);
        try {
            readProperties(context);
        } catch (IOException e) {
            throw new OptionParseException("Error reading properties.", e);
        }
        return context;
    }

    protected void readProperties(Context context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getConfigFile()));
        context.setVideoBitrate(readIntegerProperty(VIDEO_BITRATE, props));
    }

    protected int readIntegerProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Integer result = Integer.parseInt(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as an integer.");
        }

    }



    protected void parseConfigFileOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(CONFIG_FILE_OPTION.getOpt());
        if (configFileString == null) {
            parseError(CONFIG_FILE_OPTION.toString());
            throw new OptionParseException(CONFIG_FILE_OPTION.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        context.setConfigFile(configFile);
    }

    protected void parseProgramPid(CommandLine cmd) throws OptionParseException {
        String programPid = cmd.getOptionValue(PID_OPTION.getOpt());
        if (programPid == null) {
            parseError(PID_OPTION.toString());
            throw new OptionParseException(PID_OPTION.toString());
        } else {
            context.setProgrampid(programPid);
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
