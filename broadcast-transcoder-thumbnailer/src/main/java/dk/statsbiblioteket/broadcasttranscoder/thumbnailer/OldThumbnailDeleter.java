package dk.statsbiblioteket.broadcasttranscoder.thumbnailer;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 */
public class OldThumbnailDeleter extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(OldThumbnailDeleter.class);

    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {
        File thumbnailDir = FileUtils.getSnapshotOutputDir(request, context);
        final String nameElement = request.getObjectPid().replaceAll("uuid:", "");
        FilenameFilter thumbnailFilter = new MyFilenameFilter(nameElement);
        File[] thumbnails = thumbnailDir.listFiles(thumbnailFilter);
        for (File thumbnail: thumbnails) {
            if (thumbnail.delete()) {
               logger.info("Deleted " + thumbnail.getAbsolutePath());
            } else {
               logger.warn("Failed to delete " + thumbnail.getAbsolutePath());
            }
        }
        if (thumbnails.length == 0) {
            logger.info("No existing thumbnails to delete for " + request.getObjectPid());
        }
    }

    private static class MyFilenameFilter implements FilenameFilter {
        private final String nameElement;

        public MyFilenameFilter(String nameElement) {
            this.nameElement = nameElement;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.contains(nameElement);
        }
    }
}
