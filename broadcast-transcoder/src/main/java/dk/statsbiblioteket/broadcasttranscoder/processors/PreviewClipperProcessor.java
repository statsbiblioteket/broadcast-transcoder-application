/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PreviewClipperProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(PreviewClipperProcessor.class);


    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
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
            FileUtils.deleteAndLogFailedFile(outputFile, e);
            throw new ProcessorException("Process timed out for "+request.getObjectPid(),e);
        }
    }

}
