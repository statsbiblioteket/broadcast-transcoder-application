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
    public static final String X264_PARAMS = "x264Params";
    public static final String TRANSCODING_DIVISOR = "transcodingTimeoutDivisor";
    public static final String ANALYSIS_CLIP_LENGTH = "analysisCliplengthBytes";

    /*
    Properties related to Offsets
     */
    public static final String START_OFFSET_TS = "startOffsetTS";
    public static final String END_OFFSET_TS = "endOffsetTS";
    public static final String START_OFFSET_PS = "startOffsetPS";
    public static final String END_OFFSET_PS = "endOffsetPS";
    public static final String START_OFFSET_WAV = "startOffsetWAV";
    public static final String END_OFFSET_WAV = "endOffsetWAV";

    /*
    Properties relating to handling of missing data
     */
    public static final String MAX_MISSING_START = "maxMissingStart";
    public static final String MAX_MISSING_END = "maxMissingEnd";
    public static final String MAX_HOLE_SIZE = "maxHole";


    /*
    Properties related to nearline storage
     */
    public static final String FILE_FINDER = "nearlineFilefinderUrl";
    public static final String MAX_FILES_FETCHED = "maxFilesFetched";
}
