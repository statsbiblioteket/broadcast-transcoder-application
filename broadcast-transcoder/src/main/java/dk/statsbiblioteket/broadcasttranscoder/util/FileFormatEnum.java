package dk.statsbiblioteket.broadcasttranscoder.util;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/26/12
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 *
 * @see dk.statsbiblioteket.broadcasttranscoder.processors.FilePropertiesIdentifierProcessor
 */
public enum FileFormatEnum {
    /**
     * *.wav files
     */
    AUDIO_WAV,
    /**
     * *.mpg and *.mpeg files
     */
    MPEG_PS,
    /**
     * starts with mux and ends with .ts
     */
    MULTI_PROGRAM_MUX,
    /**
     * ends with .ts and bitrate > 100k
     */
    SINGLE_PROGRAM_VIDEO_TS,
    /**
     * ends with .ts and bitrate < 100k
     */
    SINGLE_PROGRAM_AUDIO_TS;
}
