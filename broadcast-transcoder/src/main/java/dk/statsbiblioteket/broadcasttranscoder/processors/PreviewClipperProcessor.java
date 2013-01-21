/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.PreviewMediaInfoDAO;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.BroadcastTypeEnum;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.db.PreviewMediaInfo;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.extraction.model.MediaTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

public class PreviewClipperProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(PreviewClipperProcessor.class);


    public PreviewClipperProcessor() {
    }

    public PreviewClipperProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        long programLength = MetadataUtils.findProgramLengthMillis(request);
        File sourceFile = FileUtils.getFinalMediaOutputFile(request, context);
        File outputDir = FileUtils.getPreviewOutputDir(request, context);
        outputDir.mkdirs();
        File outputFile = FileUtils.getPreviewOutputFile(request, context);
        String command = "ffmpeg -ss "  + programLength/(2*1000L) + " -i "
                + sourceFile.getAbsolutePath() + " -acodec copy -vcodec copy "
                + "  -t " + context.getPreviewLength() + " -y " + outputFile.getAbsolutePath();
        long timeout = context.getPreviewTimeout()*1000L;
        logger.debug("Setting preview timeout to " + timeout + " ms.");
        try {
            ExternalJobRunner.runClipperCommand(timeout, command);
        } catch (ExternalProcessTimedOutException e) {
            logger.warn("Deleting failed preview file " + outputFile.getAbsolutePath(), e);
            outputFile.delete();
            throw new ProcessorException("Process timed out for "+context.getProgrampid(),e);
        }
    }

}
