package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.BroadcastTranscoderApplication;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.*;

/**
 *
 */
public class GetJobsOptionsParser extends AbstractOptionsParser{


    protected static final Option TIMESTAMP_OPTION = new Option("timestamp", true, "The timestamp (milliseconds) for which transcoding is required");
    protected static final Option INFRASTRUCTURE_CONFIG_FILE_OPTION = new Option("infrastructure_configfile", true, "The infrastructure config file");
    protected static final Option HIBERNATE_CFG_OPTION = new Option("hibernate_configfile", true, "The hibernate config file");

    protected static final Option USAGE_OPTION = new Option("u", false, "Usage");

    protected static Options options;

    private GetJobsContext context;

    public GetJobsOptionsParser() {
        context = new GetJobsContext();
        options = new Options();
        options.addOption(TIMESTAMP_OPTION);
        options.addOption(INFRASTRUCTURE_CONFIG_FILE_OPTION);
        options.addOption(HIBERNATE_CFG_OPTION);
        options.addOption(USAGE_OPTION);
    }


    public GetJobsContext parseOptions(String[] args) throws OptionParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            parseError(e.toString());
            throw new OptionParseException(e.getMessage(), e);
        }
        parseUsageOption(cmd);
        parseInfrastructureConfigFileOption(cmd);
        parseHibernateConfigFileOption(cmd);
        parseTimestampOption(cmd);
        try {
            readInfrastructureProperties(context);
            readBehaviouralProperties(context);
        } catch (IOException e) {
            throw new OptionParseException("Error reading properties.", e);
        }
        return context;
    }




    protected void parseUsageOption(CommandLine cmd) {
         if (cmd.hasOption(USAGE_OPTION.getOpt())) {
             printUsage();
             System.exit(0);
         }
    }

    protected void readInfrastructureProperties(GetJobsContext context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getInfrastructuralConfigFile()));
        context.setDomsEndpoint(readStringProperty(DOMS_ENDPOINT, props));
        context.setDomsUsername(readStringProperty(DOMS_USER, props));
        context.setDomsPassword(readStringProperty(DOMS_PASSWORD, props));
    }

    protected void readBehaviouralProperties(GetJobsContext context) throws IOException, OptionParseException {
        Properties props = new Properties();
        //props.load(new FileInputStream(context.getBehaviourConfigFile()));
        context.setDomsViewAngle(readStringProperty(DOMS_VIEWANGLE,props));
    }



    protected void parseInfrastructureConfigFileOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(INFRASTRUCTURE_CONFIG_FILE_OPTION.getOpt());
        if (configFileString == null) {
            parseError(INFRASTRUCTURE_CONFIG_FILE_OPTION.toString());
            throw new OptionParseException(INFRASTRUCTURE_CONFIG_FILE_OPTION.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        context.setInfrastructuralConfigFile(configFile);
    }


    protected void parseHibernateConfigFileOption(CommandLine cmd) throws OptionParseException {
            String configFileString = cmd.getOptionValue(HIBERNATE_CFG_OPTION.getOpt());
            if (configFileString == null) {
                parseError(HIBERNATE_CFG_OPTION.toString());
                throw new OptionParseException(HIBERNATE_CFG_OPTION.toString());
            }
            File configFile = new File(configFileString);
            if (!configFile.exists() || configFile.isDirectory()) {
                throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
            }
            context.setHibernateConfigFile(configFile);
        }



    protected void parseTimestampOption(CommandLine cmd) throws OptionParseException {
        String timestampString = cmd.getOptionValue(TIMESTAMP_OPTION.getOpt());
        if (timestampString == null) {
            parseError(TIMESTAMP_OPTION.toString());
            throw new OptionParseException(TIMESTAMP_OPTION.toString());
        } else {
            context.setFromTimestamp(Long.parseLong(timestampString));
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
