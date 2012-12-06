package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 12/6/12
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Cleanup {

    private static Logger logger = LoggerFactory.getLogger(Cleanup.class);


    public static void main(String[] args) throws OptionParseException {
        logger.debug("Entered main method.");
        Context context = new OptionsParser().parseOptions(args);
        TranscodeRequest request = new TranscodeRequest();
        File lockFile = FileUtils.getLockFile(request, context);
        if (lockFile.exists()) {
            logger.info("Deleting " + lockFile.getAbsolutePath());
            lockFile.delete();
        }
        if (FileUtils.hasMediaOutputFile(request, context)) {
            File foundFile = FileUtils.findMediaOutputFile(request, context);
            logger.info("Deleting " + foundFile.getAbsolutePath());
            foundFile.delete();
        }
    }

}
