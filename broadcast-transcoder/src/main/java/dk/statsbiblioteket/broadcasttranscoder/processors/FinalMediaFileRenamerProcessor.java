package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 *
 */
public class FinalMediaFileRenamerProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(FinalMediaFileRenamerProcessor.class);

    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        final File tempFile = FileUtils.getTemporaryMediaOutputFile(request, context);
        final File finalFile = FileUtils.getFinalMediaOutputFile(request, context);
        logger.debug("Renaming " + tempFile.getAbsolutePath() + " to " + finalFile.getAbsolutePath());
        try {
            if (context.isOverwrite()){
                Files.move(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(tempFile.toPath(), finalFile.toPath());
            }
        } catch (IOException e) {
            final String s = "Failed to rename '" + tempFile.getAbsolutePath() + "' to '" + finalFile.getAbsolutePath()+"' . Overwrite is set to "+context.isOverwrite();
            logger.error(s,e);
            throw new ProcessorException(s,e);
        }
    }


}
