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
import java.io.IOException;
import java.util.Properties;
import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.*;

/**
 *
 */
public class OptionsParser extends AbstractOptionsParser{

    protected static final Option PID_OPTION = new Option("programpid", true, "The DOMS pid of the program to be transcoded");
    protected static final Option INFRASTRUCTURE_CONFIG_FILE_OPTION = new Option("infrastructure_configfile", true, "The infrastructure config file");
    protected static final Option BEHAVIOURAL_CONFIG_FILE_OPTION = new Option("behavioural_configfile", true, "The behavioural config file");
    protected static final Option HIBERNATE_CFG_OPTION = new Option("hibernate_configfile", true, "The hibernate config file");
    protected static final Option USAGE_OPTION = new Option("u", false, "Usage");

    protected static Options options;

    private Context context;
    private final OptionsParser abstractOptionsParser = new OptionsParser();

    public OptionsParser() {
        context = new Context();
        options = new Options();
        options.addOption(PID_OPTION);
        options.addOption(INFRASTRUCTURE_CONFIG_FILE_OPTION);
        options.addOption(BEHAVIOURAL_CONFIG_FILE_OPTION);
        options.addOption(HIBERNATE_CFG_OPTION);
        options.addOption(USAGE_OPTION);
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
        parseUsageOption(cmd);
        parseInfrastructureConfigFileOption(cmd);
        parseBehaviouralConfigFileOption(cmd);
        parseHibernateConfigFileOption(cmd);
        parseProgramPid(cmd);
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

    protected void readInfrastructureProperties(Context context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getInfrastructuralConfigFile()));
        context.setFileOutputRootdir(abstractOptionsParser.readExistingDirectoryProperty(FILE_DIR, props));
        context.setPreviewOutputRootdir(abstractOptionsParser.readExistingDirectoryProperty(PREVIEW_DIR, props));
        context.setSnapshotOutputRootdir(abstractOptionsParser.readExistingDirectoryProperty(SNAPSHOT_DIR, props));
        context.setLockDir(abstractOptionsParser.readExistingDirectoryProperty(LOCK_DIR, props));
        context.setFileDepth(abstractOptionsParser.readIntegerProperty(FILE_DEPTH, props));
        context.setFileFinderUrl(abstractOptionsParser.readStringProperty(FILE_FINDER, props));
        context.setMaxFilesFetched(abstractOptionsParser.readIntegerProperty(MAX_FILES_FETCHED, props));
        context.setDomsEndpoint(abstractOptionsParser.readStringProperty(DOMS_ENDPOINT, props));
        context.setDomsUsername(abstractOptionsParser.readStringProperty(DOMS_USER, props));
        context.setDomsPassword(abstractOptionsParser.readStringProperty(DOMS_PASSWORD, props));
    }

    protected void readBehaviouralProperties(Context context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getBehaviourConfigFile()));
        context.setVideoBitrate(abstractOptionsParser.readIntegerProperty(VIDEO_BITRATE, props));
        context.setAudioBitrate(abstractOptionsParser.readIntegerProperty(AUDIO_BITRATE, props));
        context.setVideoHeight(abstractOptionsParser.readIntegerProperty(HEIGHT, props));
        context.setX264VlcParams(abstractOptionsParser.readStringProperty(X264_VLC_PARAMS, props));
        context.setX264FfmpegParams(abstractOptionsParser.readStringProperty(X264_FFMPEG_PARAMS, props));
        context.setTranscodingTimeoutDivisor(abstractOptionsParser.readIntegerProperty(TRANSCODING_DIVISOR, props));
        context.setAnalysisClipLength(abstractOptionsParser.readLongProperty(ANALYSIS_CLIP_LENGTH, props));
        context.setStartOffsetTS(abstractOptionsParser.readIntegerProperty(START_OFFSET_TS, props));
        context.setEndOffsetTS(abstractOptionsParser.readIntegerProperty(END_OFFSET_TS, props));
        context.setStartOffsetPS(abstractOptionsParser.readIntegerProperty(START_OFFSET_PS, props));
        context.setEndOffsetPS(abstractOptionsParser.readIntegerProperty(END_OFFSET_PS, props));
        context.setStartOffsetWAV(abstractOptionsParser.readIntegerProperty(START_OFFSET_WAV, props));
        context.setEndOffsetWAV(abstractOptionsParser.readIntegerProperty(END_OFFSET_WAV, props));
        context.setMaxMissingStart(abstractOptionsParser.readIntegerProperty(MAX_MISSING_START, props));
        context.setMaxMissingEnd(abstractOptionsParser.readIntegerProperty(MAX_MISSING_END, props));
        context.setMaxHole(abstractOptionsParser.readIntegerProperty(MAX_HOLE_SIZE, props));
        context.setGapToleranceSeconds(abstractOptionsParser.readIntegerProperty(GAP_TOLERANCE, props));
        context.setPreviewLength(abstractOptionsParser.readIntegerProperty(PREVIEW_LENGTH, props));
        context.setPreviewTimeout(abstractOptionsParser.readIntegerProperty(PREVIEW_TIMEOUT, props));
        context.setSnapshotFrames(abstractOptionsParser.readIntegerProperty(SNAPSHOT_FRAMES, props));
        context.setSnapshotPaddingSeconds(abstractOptionsParser.readIntegerProperty(SNAPSHOT_PADDING, props));
        context.setSnapshotScale(abstractOptionsParser.readIntegerProperty(SNAPSHOT_SCALE, props));
        context.setSnapshotTargetDenominator(abstractOptionsParser.readIntegerProperty(SNAPSHOT_TARGET_DENOMINATIOR, props));
        context.setSnapshotTargetNumerator(abstractOptionsParser.readIntegerProperty(SNAPSHOT_TARGET_NUMERATOR, props));
        context.setSnapshotTimeoutDivisor(abstractOptionsParser.readIntegerProperty(SNAPSHOT_TIMEOUT_DIVISOR, props));
        context.setSoxTranscodeParams(abstractOptionsParser.readStringProperty(SOX_TRANSCODE_PARAMS, props));
    }

    protected File readExistingDirectoryProperty (String propName, Properties props) throws OptionParseException {
        return abstractOptionsParser.readExistingDirectoryProperty(propName, props);
    }

    protected String readStringProperty (String propName, Properties props) throws OptionParseException {
        return abstractOptionsParser.readStringProperty(propName, props);
    }

    protected int readIntegerProperty(String propName, Properties props) throws OptionParseException {
        return abstractOptionsParser.readIntegerProperty(propName, props);
    }

     protected long readLongProperty(String propName, Properties props) throws OptionParseException {
         return abstractOptionsParser.readLongProperty(propName, props);
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
            context.setBehaviourConfigFile(configFile);
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
