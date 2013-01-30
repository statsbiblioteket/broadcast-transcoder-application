package dk.statsbiblioteket.broadcasttranscoder.cli.parsers;

import dk.statsbiblioteket.broadcasttranscoder.cli.HibernateContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.contexts.FetcherContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.parsers.HibernateOptionsParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.*;
import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.REKLAMEFILM_ROOT_DIRECTORY_LIST;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/30/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class InfrastructureOptionsParser<T> extends HibernateOptionsParser<T> {

    protected static final Option INFRASTRUCTURE_CONFIG_FILE_OPTION = new Option("infrastructure_configfile", true, "The infrastructure config file");

    protected InfrastructureOptionsParser() {
        super();
        getOptions().addOption(INFRASTRUCTURE_CONFIG_FILE_OPTION);

    }

    protected void readInfrastructureProperties(InfrastructureContext<T> context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getInfrastructuralConfigFile()));

        /*Doms*/
        context.setDomsEndpoint(readStringProperty(DOMS_ENDPOINT, props));
        context.setDomsUsername(readStringProperty(DOMS_USER, props));
        context.setDomsPassword(readStringProperty(DOMS_PASSWORD, props));

        /*output dirs*/
        context.setFileOutputRootdir(readFileProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.FILE_DIR, props));
        context.setPreviewOutputRootdir(readFileProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.PREVIEW_DIR, props));
        context.setSnapshotOutputRootdir(readFileProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.SNAPSHOT_DIR, props));
        context.setFileFinderUrl(readStringProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.FILE_FINDER, props));
        context.setMaxFilesFetched(readIntegerProperty(dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.MAX_FILES_FETCHED, props));

        context.setLockDir(readExistingDirectoryProperty(LOCK_DIR, props));
        context.setFileDepth(readIntegerProperty(FILE_DEPTH, props));

        try {
            context.setReklamefileRootDirectories(readStringProperty(REKLAMEFILM_ROOT_DIRECTORY_LIST, props).split(","));
        } catch (OptionParseException e) {
            context.setReklamefileRootDirectories(new String[]{});
        }



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
        getContext().setInfrastructuralConfigFile(configFile);
    }

    @Override
    protected abstract InfrastructureContext<T> getContext();


}
