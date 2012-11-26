package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Ideally this information should be readily extractable from the supplied metadata.
 */
public class FilePropertiesIdentifierProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FilePropertiesIdentifierProcessor.class);

    public FilePropertiesIdentifierProcessor() {
    }

    public FilePropertiesIdentifierProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        //Assume all files have same format and bitrate or we've lost before we start.
        Map.Entry<BroadcastMetadata, File> entry =  request.getFileMap().entrySet().iterator().next();
        Long fileLength = entry.getValue().length();  //bytes
        Long duration = entry.getKey().getStopTime().toGregorianCalendar().getTimeInMillis()
                - entry.getKey().getStartTime().toGregorianCalendar().getTimeInMillis();
        Long bitrate = fileLength/(duration/1000L) ;
        logger.info("Setting bitrate for " + context.getProgrampid() + " to " + bitrate + " bytes/second");
        request.setBitrate(bitrate);
        String filename = entry.getKey().getFilename();
        FileFormatEnum format = null;
        if (filename.endsWith("wav")) {
            format = FileFormatEnum.AUDIO_WAV;
        } else if (filename.endsWith("mpeg")) {
            format = FileFormatEnum.MPEG_PS;
        } else if (filename.startsWith("mux") && filename.endsWith("ts")) {
            format = FileFormatEnum.MULTI_PROGRAM_MUX;
        } else if (filename.endsWith("mux")) {
            if (bitrate > 100000l) {
                format = FileFormatEnum.SINGLE_PROGRAM_VIDEO_TS;
            } else {
                format = FileFormatEnum.SINGLE_PROGRAM_AUDIO_TS;
            }
        }
        if (format == null) {
            throw new ProcessorException("Could not identify format of " + entry.getValue().getAbsolutePath());
        } else {
            logger.info("Identified format of " + entry.getValue().getAbsolutePath() + " as " + format);
            request.setFileFormat(format);
        }
    }
}
