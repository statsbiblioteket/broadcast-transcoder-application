package dk.statsbiblioteket.broadcasttranscoder.cli.parsers;

import dk.statsbiblioteket.broadcasttranscoder.cli.GetJobsContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Properties;

import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.*;

/**
 *
 */
public class GetJobsOptionsParser<T> extends InfrastructureOptionsParser<T> {


    protected static final Option TIMESTAMP_OPTION = new Option("timestamp", true, "The timestamp (milliseconds) for which transcoding is required");



    private GetJobsContext<T> context;

    public GetJobsOptionsParser() {
        context = new GetJobsContext<T>();
        getOptions().addOption(TIMESTAMP_OPTION);
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
        //props.load(new FileInputStream(context.getBehaviourConfigFile()));
        context.setDomsViewAngle(readStringProperty(DOMS_VIEWANGLE,props));
        context.setCollection(readStringProperty(DOMS_COLLECTION,props));
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
