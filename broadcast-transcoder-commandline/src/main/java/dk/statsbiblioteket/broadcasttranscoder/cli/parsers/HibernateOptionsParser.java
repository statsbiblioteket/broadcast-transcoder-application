package dk.statsbiblioteket.broadcasttranscoder.cli.parsers;

import dk.statsbiblioteket.broadcasttranscoder.cli.AbstractOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.cli.HibernateContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.TranscodingRecordDao;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscoderPersistenceProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/30/13
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class HibernateOptionsParser<T> extends AbstractOptionsParser {

    protected static final Option HIBERNATE_CFG_OPTION = new Option("hibernate_configfile", true, "The hibernate config file");

    protected HibernateOptionsParser() {
        super();
        getOptions().addOption(HIBERNATE_CFG_OPTION);

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

        getContext().setHibernateConfigFile(configFile);



    }

    protected abstract HibernateContext<T> getContext();




}
