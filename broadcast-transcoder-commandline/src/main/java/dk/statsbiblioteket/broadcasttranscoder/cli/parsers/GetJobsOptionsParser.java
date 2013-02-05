package dk.statsbiblioteket.broadcasttranscoder.cli.parsers;

import dk.statsbiblioteket.broadcasttranscoder.cli.GetJobsContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.*;

/**
 *
 */
public class GetJobsOptionsParser<T> extends InfrastructureOptionsParser<T> {


    protected static final Option TIMESTAMP_OPTION = new Option("timestamp", true, "The timestamp (milliseconds) for which transcoding is required");
    protected static final Option BEHAVIOURAL_CONFIG_FILE_OPTION = new Option("behavioural_configfile", true, "The behavioural config file");



    private GetJobsContext<T> context;
    private File configFile;

    public GetJobsOptionsParser() {
        context = new GetJobsContext<T>();
        getOptions().addOption(TIMESTAMP_OPTION);
        getOptions().addOption(BEHAVIOURAL_CONFIG_FILE_OPTION);
    }



    public GetJobsContext<T> parseOptions(String[] args) throws OptionParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException e) {
            parseError(e.toString());
            throw new OptionParseException(e.getMessage(), e);
        }
        parseUsageOption(cmd);
        parseInfrastructureConfigFileOption(cmd);
        parseHibernateConfigFileOption(cmd);
        parseTimestampOption(cmd);
        parseBehaviouralConfigFileOption(cmd);
        try {
            readInfrastructureProperties(context);
            readBehaviouralProperties(context);
        } catch (IOException e) {
            throw new OptionParseException("Error reading properties.", e);
        }
        return context;
    }






    protected void readBehaviouralProperties(GetJobsContext<T> context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(configFile));
        //context.setDomsViewAngle(readStringProperty(DOMS_VIEWANGLE,props));
        context.setCollection(readStringProperty(DOMS_COLLECTION,props));
        context.setState(TranscodingStateEnum.PENDING);
    }

    protected void parseBehaviouralConfigFileOption(CommandLine cmd) throws OptionParseException {
          String configFileString = cmd.getOptionValue(BEHAVIOURAL_CONFIG_FILE_OPTION.getOpt());
          if (configFileString == null) {
              parseError(BEHAVIOURAL_CONFIG_FILE_OPTION.toString());
              throw new OptionParseException(BEHAVIOURAL_CONFIG_FILE_OPTION.toString());
          }
          configFile = new File(configFileString);
          if (!configFile.exists() || configFile.isDirectory()) {
              throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
          }
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


    @Override
    protected GetJobsContext<T> getContext() {
        return context;
    }
}
