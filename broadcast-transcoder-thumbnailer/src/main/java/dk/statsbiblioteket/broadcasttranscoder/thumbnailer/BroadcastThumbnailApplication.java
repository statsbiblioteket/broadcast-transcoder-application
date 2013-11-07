package dk.statsbiblioteket.broadcasttranscoder.thumbnailer;

import dk.statsbiblioteket.broadcasttranscoder.TranscoderApplication;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.UsageException;
import dk.statsbiblioteket.broadcasttranscoder.cli.parsers.SingleTranscodingOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An easy-to-use application which takes the uuid of an already-transcoded TV program, deletes any existing thumbnails,
 * and generates a new set of thumbnails.
 */
public class BroadcastThumbnailApplication extends TranscoderApplication {

    private static Logger logger = LoggerFactory.getLogger(BroadcastThumbnailApplication.class);


    public static void main(String[] args) throws OptionParseException {
        logger.debug("Entered main method.");
        SingleTranscodingContext<BroadcastTranscodingRecord> context = null;
        TranscodeRequest request = null;
        try {
            try {
                context = new SingleTranscodingOptionsParser<BroadcastTranscodingRecord>().parseOptions(args);
            } catch (UsageException e) {
                return;
            }
            //HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
            //context.setTranscodingProcessInterface(new BroadcastTranscodingRecordDAO(util));
            request = new TranscodeRequest();
            request.setObjectPid(context.getProgrampid());
        } catch (Exception e) {
            logger.error("Error in initial environment", e);
            throw new OptionParseException("Failed to parse optioons",e);
        }
        if (!FileUtils.hasFinalMediaOutputFile(request, context)) {
            logger.warn("Cannot generate snapshots as there is no transcoded file for " + request.getObjectPid());
            System.exit(2);
        } else {
            //Delete existing files
            //Calculate resolution and aspect ratio of transcoded file
            //Do thumbnails
        }
    }
}
