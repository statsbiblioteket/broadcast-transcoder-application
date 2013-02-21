package dk.statsbiblioteket.broadcasttranscoder.cli.parsers;

import dk.statsbiblioteket.broadcasttranscoder.cli.*;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.Properties;

import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.*;
import static dk.statsbiblioteket.broadcasttranscoder.cli.PropertyNames.OVERWRITE;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/4/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgramAnalyzerOptionsParser<T extends TranscodingRecord> extends InfrastructureOptionsParser<T> {


    protected static final Option PROGRAMS = new Option("programList", true, "The list of programns to analyze");
    protected static final Option FILESIZES = new Option("fileSizes", true, "The list of filenames and sizes");
    protected static final Option BEHAVIOURAL_CONFIG_FILE_OPTION = new Option("behavioural_configfile", true, "The behavioural config file");


    private ProgramAnalyzerContext<T> context;


    public ProgramAnalyzerOptionsParser() {
        super();
        context = new ProgramAnalyzerContext<T>();
        getOptions().addOption(PROGRAMS);
        getOptions().addOption(FILESIZES);
        getOptions().addOption(BEHAVIOURAL_CONFIG_FILE_OPTION);
    }

    public ProgramAnalyzerContext<T> parseOptions(String[] args) throws OptionParseException, UsageException {
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
        parseBehaviouralConfigFileOption(cmd);
        parseProgramsListOption(cmd);
        parseFileLengthListOption(cmd);
        try {
            readInfrastructureProperties(context);
            readBehaviouralProperties(context);
        } catch (IOException e) {
            throw new OptionParseException("Error reading properties.", e);
        }
        return context;
    }

    private void parseFileLengthListOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(FILESIZES.getOpt());
        if (configFileString == null) {
            parseError(FILESIZES.toString());
            throw new OptionParseException(FILESIZES.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line;

            while ((line = reader.readLine()) != null ) {
                String[] splits;
                splits = line.split(" ");
                String filename = splits[0];
                Long size = Long.parseLong(splits[1]);
                getContext().getFileLengthList().put(filename,size);
            }
        } catch (FileNotFoundException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        } catch (IOException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not readable.");
        }
    }

    private void parseProgramsListOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(PROGRAMS.getOpt());
        if (configFileString == null) {
            parseError(PROGRAMS.toString());
            throw new OptionParseException(PROGRAMS.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = reader.readLine()) != null ){
                String pid = line.trim();
                if ( ! pid.isEmpty()){
                    getContext().getPidList().add(pid);
                }
            }
        } catch (FileNotFoundException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        } catch (IOException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not readable.");
        }
    }


    protected void readBehaviouralProperties(SingleTranscodingContext context) throws IOException, OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getBehaviourConfigFile()));
        context.setVideoBitrate(readIntegerProperty(VIDEO_BITRATE, props));
        context.setAudioBitrate(readIntegerProperty(AUDIO_BITRATE, props));
        context.setVideoHeight(readIntegerProperty(HEIGHT, props));
        context.setVlcTranscodingString(readStringProperty(VLC_TRANSCODING_STRING, props));
        context.setX264FfmpegParams(readStringProperty(X264_FFMPEG_PARAMS, props));
        context.setTranscodingTimeoutDivisor(readFloatProperty(TRANSCODING_DIVISOR, props));
        context.setAnalysisClipLength(readLongProperty(ANALYSIS_CLIP_LENGTH, props));
        context.setStartOffsetTS(readIntegerProperty(START_OFFSET_TS, props));
        context.setEndOffsetTS(readIntegerProperty(END_OFFSET_TS, props));
        context.setStartOffsetTSWithTVMeter(readIntegerProperty(START_OFFSET_TS_WITH_TVMETER, props));
        context.setEndOffsetTSWithTVMeter(readIntegerProperty(END_OFFSET_TS_WITH_TVMETER, props));
        context.setStartOffsetPS(readIntegerProperty(START_OFFSET_PS, props));
        context.setEndOffsetPS(readIntegerProperty(END_OFFSET_PS, props));
        context.setStartOffsetPSWithTVMeter(readIntegerProperty(START_OFFSET_PS_WITH_TVMETER, props));
        context.setEndOffsetPSWithTVMeter(readIntegerProperty(END_OFFSET_PS_WITH_TVMETER, props));
        context.setStartOffsetWAV(readIntegerProperty(START_OFFSET_WAV, props));
        context.setEndOffsetWAV(readIntegerProperty(END_OFFSET_WAV, props));
        context.setMaxMissingStart(readIntegerProperty(MAX_MISSING_START, props));
        context.setMaxMissingEnd(readIntegerProperty(MAX_MISSING_END, props));
        context.setMaxHole(readIntegerProperty(MAX_HOLE_SIZE, props));
        context.setGapToleranceSeconds(readIntegerProperty(GAP_TOLERANCE, props));
        context.setPreviewLength(readIntegerProperty(PREVIEW_LENGTH, props));
        context.setPreviewTimeout(readIntegerProperty(PREVIEW_TIMEOUT, props));
        context.setSnapshotFrames(readIntegerProperty(SNAPSHOT_FRAMES, props));
        context.setSnapshotPaddingSeconds(readIntegerProperty(SNAPSHOT_PADDING, props));
        context.setSnapshotScale(readIntegerProperty(SNAPSHOT_SCALE, props));
        context.setSnapshotTargetDenominator(readIntegerProperty(SNAPSHOT_TARGET_DENOMINATIOR, props));
        context.setSnapshotTargetNumerator(readIntegerProperty(SNAPSHOT_TARGET_NUMERATOR, props));
        context.setSnapshotTimeoutDivisor(readFloatProperty(SNAPSHOT_TIMEOUT_DIVISOR, props));
        context.setSoxTranscodeParams(readStringProperty(SOX_TRANSCODE_PARAMS, props));
        context.setDefaultTranscodingTimestamp(readLongProperty(DEFAULT_TIMESTAMP, props));
        context.setOverwrite(readBooleanProperty(OVERWRITE,props));
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


    @Override
    protected ProgramAnalyzerContext<T> getContext() {
        return context;
    }


}
