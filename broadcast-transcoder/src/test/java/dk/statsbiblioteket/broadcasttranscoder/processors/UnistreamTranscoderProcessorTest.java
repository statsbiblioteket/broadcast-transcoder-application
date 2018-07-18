package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Properties;

public class UnistreamTranscoderProcessorTest {
    
    @Test
    public void getFfmpegCommandLine() throws URISyntaxException, IOException, OptionParseException,
            ProcessorException {
        TranscodeRequest request = new TranscodeRequest();
    
        
    /*
    Input #0, mpeg, from '/bitarkiv/0400/files/tv3_S09-TV3_S09-TV3_mpeg1_20111113045601_20111114045502_encoder6-2.mpeg':
    Duration: 23:58:53.44, start: 0.276167, bitrate: 1354 kb/s
        Stream #0:0[0x1e0]: Video: mpeg1video, yuv420p(tv), 352x288 [SAR 49:33 DAR 49:27], 1150 kb/s, 25 fps, 25 tbr, 90k tbn, 25 tbc
        Stream #0:1[0x1c0]: Audio: mp2, 48000 Hz, stereo, s16p, 192 kb/s

    */
        request.setFileFormat(FileFormatEnum.MPEG_PS);
        request.setObjectPid("uuid:457513af-30c6-477a-8c3e-b8b19d0fbaac");
        request.setDisplayAspectRatio(49.0/27);
        request.setAudioStereoPid("01c0");
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip("/bitarkiv/0400/files/tv3_S09-TV3_S09-TV3_mpeg1_20111113045601_20111114045502_encoder6-2.mpeg");
        clip.setFileEndTime(1321242902000L);
        clip.setFileStartTime(1321156561000L);
        clip.setStartOffsetBytes(6529341434L);
        request.setClips(Arrays.asList(clip));
        request.setTvmeter(true);
    
        request.setProgramBroadcast(getProgramBroadcast(request,null,
                                                        "<programBroadcast xmlns=\"http://doms.statsbiblioteket.dk/types/program_broadcast/0/1/#\">\n"
                                                                     + "  <timeStart>2011-11-13T15:40:00.000+01:00</timeStart>\n"
                                                                     + "  <timeStop>2011-11-13T16:25:00.000+01:00</timeStop>\n"
                                                                     + "  <channelId>tv3</channelId>\n"
                                                                     + "</programBroadcast>"));
        
        SingleTranscodingContext<TranscodingRecord> context = new SingleTranscodingContext<>();
        context.setBehaviourConfigFile(new File(Thread.currentThread().getContextClassLoader().getResource("bta.behaviour.properties").toURI()));
        readBehaviouralProperties(context);
        
    
        String commandLine = UnistreamTranscoderProcessor.getFfmpegCommandLine(request, context);
//        String expectedCommandLine
//                = "ffmpeg -ss 38574 -t 2820 -i concat:/bitarkiv/0400/files/tv3_S09-TV3_S09-TV3_mpeg1_20111113045601_20111114045502_encoder6-2.mpeg -ss 00:00:05 -async 2 -vcodec libx264 -deinterlace -ar 44100 -profile:v High -level 3.0 -preset superfast -threads 0 -b:v 400000 -ac 2 -b:a 96000 -y -filter_complex [0:v]scale= 522x288 /home/bta/streamingContent/temp/457513af-30c6-477a-8c3e-b8b19d0fbaac.flv";
        String expectedCommandLine = "ffmpeg -ss 0 -t 2820 -f concat -safe 0 -i <(echo -e \"file '/bitarkiv/0400/files/tv3_S09-TV3_S09-TV3_mpeg1_20111113045601_20111114045502_encoder6-2.mpeg' \\ninpoint 38574 \\n\") -ss 5 -async 2 -vcodec libx264 -deinterlace -ar 44100 -profile:v High -level 3.0 -preset superfast -map 0:a:#01c0 -threads 0 -b:v 400000 -ac 2 -b:a 96000 -y -filter_complex \"[0:v]scale=522x288\" /temp/457513af-30c6-477a-8c3e-b8b19d0fbaac.flv";
        Assert.assertEquals(expectedCommandLine,commandLine);
        
    }
    
    
    private ProgramBroadcast getProgramBroadcast(TranscodeRequest request,
                                                 InfrastructureContext context,
                                                 String broadcastXmlString) throws ProcessorException {
        
        ProgramBroadcast programBroadcast = null;
        try {
            //final URL resource = getClass().getClassLoader().getResource("ProgramBroadCast.xsd");
            JAXBContext jaxbContext = JAXBContext.newInstance(ProgramBroadcast.class.getPackage().getName());
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            
            Object jaxbthing = unmarshaller.unmarshal(new StringReader(broadcastXmlString));
            programBroadcast = ((JAXBElement<ProgramBroadcast>)jaxbthing).getValue();
            
            programBroadcast = JAXBContext.newInstance(ProgramBroadcast.class).createUnmarshaller().unmarshal(new StreamSource(new StringReader(broadcastXmlString)), ProgramBroadcast.class).getValue();
            
            //JaxbWrapper<ProgramBroadcast> programBroadcastWrapper = new JaxbWrapper<ProgramBroadcast>(resource,ProgramBroadcast.class);
            //programBroadcast = programBroadcastWrapper.xmlToObject(broadcastXmlString);
        } catch (Exception e) {
            throw new ProcessorException("Fault barrier for "+request.getObjectPid(),e);
        }
        return programBroadcast;
    }
    
    protected static void readBehaviouralProperties(SingleTranscodingContext context) throws
            IOException,
            OptionParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(context.getBehaviourConfigFile()));
        context.setVideoBitrate(readIntegerProperty(VIDEO_BITRATE, props));
        context.setAudioBitrate(readIntegerProperty(AUDIO_BITRATE, props));
        context.setVideoHeight(readIntegerProperty(HEIGHT, props));
        context.setVlcTranscodingString(readStringProperty(VLC_TRANSCODING_STRING, props));
        context.setFfmpegTranscodingWithSubtitlesString(readStringProperty(FFMPEG_TRANSCODING_WITH_SUBTITLES_STRING, props));
        context.setFfmpegTranscodingString(readStringProperty(FFMPEG_TRANSCODING_STRING, props));
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
        context.setOnlyTranscodeChanges(readBooleanProperty(ONLYTRANSCODECHANGES, props));
        context.setVideoOutputSuffix(readStringProperty(VIDEO_OUTPUT_SUFFIX, props));
        context.setVlcRemuxingString(readStringProperty(VLC_REMUXING_STRING, props));
        context.setDomsViewAngle(readStringProperty(DOMS_VIEWANGLE, props));
    }
    /*
      Properties related to DOMS
       */
    public static final String DOMS_ENDPOINT = "domsWSAPIEndpointUrl";
    public static final String DOMS_USER = "domsUsername";
    public static final String DOMS_PASSWORD = "domsPassword";
    public static final String DOMS_VIEWANGLE = "domsViewAngle";
    public static final String DOMS_COLLECTION = "collection";
    
    /*
    Properties related to file paths
     */
    public static final String FILE_DIR = "fileOutputDirectory";
    public static final String PREVIEW_DIR = "previewOutputDirectory";
    public static final String SNAPSHOT_DIR = "snapshotOutputDirectory";
    public static final String LOCK_DIR = "lockDirectory";
    public static final String FILE_DEPTH = "fileDepth";
    
    
    /*
    Properties related to transcoding
     */
    public static final String VIDEO_BITRATE = "videoBitrate";
    public static final String AUDIO_BITRATE = "audioBitrate";
    public static final String HEIGHT = "heightInPixels";
    public static final String VLC_TRANSCODING_STRING = "vlcTranscodingString";
    public static final String FFMPEG_TRANSCODING_STRING = "ffmpegTranscodingString";
    public static final String FFMPEG_TRANSCODING_WITH_SUBTITLES_STRING = "ffmpegTranscodingWithSubtitlesString";
    public static final String VLC_REMUXING_STRING = "vlcRemuxingString";
    public static final String TRANSCODING_DIVISOR = "transcodingTimeoutDivisor";
    public static final String ANALYSIS_CLIP_LENGTH = "analysisCliplengthBytes";
    public static final String VIDEO_OUTPUT_SUFFIX = "videoOutputSuffix";
    
    public static final String VIEW_ANGLE = "viewAngle";
    public static final String COLLECTION = "collection";
    public static final String STATE = "state";
    public static final String BATCH_SIZE = "batchSize";
    
    /*
    Properties related to Offsets
     */
    public static final String START_OFFSET_TS = "startOffsetTS";
    public static final String END_OFFSET_TS = "endOffsetTS";
    public static final String START_OFFSET_PS = "startOffsetPS";
    public static final String END_OFFSET_PS = "endOffsetPS";
    public static final String START_OFFSET_WAV = "startOffsetWAV";
    public static final String END_OFFSET_WAV = "endOffsetWAV";
    public static final String START_OFFSET_TS_WITH_TVMETER = "startOffsetTSWithTVMeter";
    public static final String END_OFFSET_TS_WITH_TVMETER = "endOffsetTSWithTVMeter";
    public static final String START_OFFSET_PS_WITH_TVMETER = "startOffsetPSWithTVMeter";
    public static final String END_OFFSET_PS_WITH_TVMETER = "endOffsetPSWithTVMeter";
    
    /*
    Properties relating to handling of missing data
     */
    public static final String MAX_MISSING_START = "maxMissingStart";
    public static final String MAX_MISSING_END = "maxMissingEnd";
    public static final String MAX_HOLE_SIZE = "maxHole";
    public static final String GAP_TOLERANCE = "gapToleranceSeconds";
    
    
    /*
    Properties related to nearline storage
     */
    public static final String NEARLINE_FILEFINDER_URL = "nearlineFilefinderUrl";
    public static final String ONLINE_FILEFINDER_URL = "onlineFilefinderUrl";
    public static final String MAX_FILES_FETCHED = "maxFilesFetched";
    
    /*
    Properties relating to previews
     */
    public static final String PREVIEW_LENGTH = "previewLength";
    public static final String PREVIEW_TIMEOUT = "previewTimeout";
    
    /*
    Properties relating to snapshots
     */
    public static final String SNAPSHOT_SCALE = "snapshotScale";
    public static final String SNAPSHOT_TARGET_NUMERATOR = "snapshotTargetNumerator";
    public static final String SNAPSHOT_TARGET_DENOMINATIOR = "snapshotTargetDenominator";
    public static final String SNAPSHOT_FRAMES = "snapshotFrames";
    public static final String SNAPSHOT_PADDING = "snapshotPaddingSeconds";
    public static final String SNAPSHOT_TIMEOUT_DIVISOR = "snapshotTimeoutDivisor";
    
    public static final String SOX_TRANSCODE_PARAMS = "soxTranscodeParams";
    
    public static final String DEFAULT_TIMESTAMP= "defaultTranscodingTimestamp";
    
    public static final String OVERWRITE = "overwrite";
    public static final String ONLYTRANSCODECHANGES = "onlyTranscodeChanges";
    
    /**
     * Properties relating to reklamefilm
     */
    public static final String REKLAMEFILM_ROOT_DIRECTORY_LIST = "reklamefilmRootDirectories";
    
    protected static File readFileProperty(String propName, Properties props)  throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        return new File(prop);
    }
    
    protected static File readExistingDirectoryProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        File dir = new File(prop);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new OptionParseException(dir.getAbsolutePath() + " must be a pre-existing directory");
        }
        return dir;
    }
    
    protected static String readStringProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        return prop;
    }
    
    protected static int readIntegerProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Integer result = Integer.parseInt(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as an integer.");
        }
    }
    
    protected static float readFloatProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Float result = Float.parseFloat(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as a float.");
        }
    }
    
    protected static long readLongProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Long result = Long.parseLong(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as a long.");
        }
    }
    
    protected static boolean readBooleanProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            boolean result = Boolean.parseBoolean(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as a boolean.");
        }
    }
}