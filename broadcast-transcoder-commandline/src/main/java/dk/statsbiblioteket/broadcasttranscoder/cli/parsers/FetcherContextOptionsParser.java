package dk.statsbiblioteket.broadcasttranscoder.cli.parsers;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.contexts.FetcherContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.ReklamefilmTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.TranscodingProcessInterface;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.TranscodingRecordDao;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class FetcherContextOptionsParser<T> extends InfrastructureOptionsParser<T> {

    protected static final Option SINCE = new Option("since", true, "The timestamp to start from");
    protected static final Option BEHAVIOURAL_CONFIG_FILE_OPTION = new Option("behavioural_configfile", true, "The behavioural config file");


    private FetcherContext<T> context;

    public FetcherContextOptionsParser() {
        super();
        context = new FetcherContext<T>();
        getOptions().addOption(SINCE);
        getOptions().addOption(BEHAVIOURAL_CONFIG_FILE_OPTION);
    }

    @Override
    protected FetcherContext<T> getContext() {
        return context;
    }


    public FetcherContext<T> parseOptions(String[] args) throws OptionParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException e) {
            parseError(e.toString());
            throw new OptionParseException(e.getMessage(), e);
        }
        parseUsageOption(cmd);
        parseSince(cmd);
        parseInfrastructureConfigFileOption(cmd);
        parseBehaviouralConfigFileOption(cmd);
        parseHibernateConfigFileOption(cmd);
        try {
            readInfrastructureProperties(context);
            readFetcherProperties(context);
        } catch (IOException e) {
            throw new OptionParseException("Error reading properties.", e);
        }
        return context;
    }

    private void parseSince(CommandLine cmd) throws OptionParseException {
        String timestampString = cmd.getOptionValue(SINCE.getOpt());
        if (timestampString == null) {
            parseError(SINCE.toString());
            throw new OptionParseException(SINCE.toString());
        } else {
            context.setSince(Long.parseLong(timestampString));
        }

    }



    protected void readFetcherProperties(FetcherContext<T> context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getFetcherFile()));
        context.setViewAngle(readStringProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.VIEW_ANGLE,props));
        context.setCollection(readStringProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.COLLECTION, props));
        context.setFedoraState(readStringProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.STATE, props));
        context.setBatchSize(readIntegerProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.BATCH_SIZE, props));
    }


    protected void parseBehaviouralConfigFileOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(BEHAVIOURAL_CONFIG_FILE_OPTION.getOpt());
        if (configFileString == null) {
            parseError(BEHAVIOURAL_CONFIG_FILE_OPTION.toString());
            throw new OptionParseException(BEHAVIOURAL_CONFIG_FILE_OPTION.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        context.setFetcherFile(configFile);
    }


}
