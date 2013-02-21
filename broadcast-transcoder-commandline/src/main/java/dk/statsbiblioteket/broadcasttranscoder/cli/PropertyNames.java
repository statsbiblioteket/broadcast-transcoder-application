/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.cli;

public class PropertyNames {

    private PropertyNames(){}

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
    public static final String X264_FFMPEG_PARAMS = "x264FfmpegProgramStreamParams";
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
    public static final String FILE_FINDER = "nearlineFilefinderUrl";
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
}
