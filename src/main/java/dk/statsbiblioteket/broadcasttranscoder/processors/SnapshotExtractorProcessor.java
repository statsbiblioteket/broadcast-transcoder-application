package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 * https://sbprojects.statsbiblioteket.dk/jira/browse/KULTUR-136
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 10/1/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 *
 ffmpeg -i ./f/6/f/d/f6fdec8f-bde5-4795-8688-f3d5f8a1d540.flv -ss 120 -r 5/1557-an -s 312x234
 -vf 'pad=416:234:52:0:black' -an outdir/file.%d.bmp

 */
public class SnapshotExtractorProcessor extends ProcessorChainElement {

    private static final Logger logger = LoggerFactory.getLogger(SnapshotExtractorProcessor.class);

   //TODO all these parameters to config files.
    private static final int scale = 26; //multiply by 16x9 for final size

    private static final int nframes = 5;

    private static final int paddingSeconds = 120;

    private static final int targetNumerator = 16;

    private static final int targetDenominator = 9;

    private static final double targetAspectRatio = (1.*targetNumerator)/(1.*targetDenominator);

    private static final long timeout = 10*60*1000L;

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {

        File fullMediaFile = FileUtils.getMediaOutputFile(request, context);
        String commandLine = "ffmpeg -i " + fullMediaFile.getAbsolutePath();
        Double aspectRatio = request.getDisplayAspectRatio();
        int N = targetNumerator * scale;
        int M = targetDenominator * scale;
        String geometry = "";
        if (Math.abs(aspectRatio - targetAspectRatio) < 0.01) {
            geometry = " -s " + N + "x" + M;
        } else if (aspectRatio - targetAspectRatio > 0.01) {
            double delta = (N - aspectRatio * M)/2.;
            int Nprime = (int) Math.round(aspectRatio * M);
            geometry = " -s " + Nprime + "x" + M +
                    " -vf 'pad=" + N + ":" + M + ":" + delta + ":0:black' ";
        } else if (targetAspectRatio - aspectRatio < 0.01) {
            int Mprime = (int) Math.round(M/aspectRatio);
            int delta = (M - Mprime)/2;
            geometry = " -s " + N + "x" + Mprime +
                    " -vf 'pad="  + N + ":" + M + ":0:" + delta + ":black' ";
        }

        int length = (int) (MetadataUtils.findProgramLengthMillis(request)/1000L);
        int modLength = length - 2*paddingSeconds;
        String rate = nframes + "/" + modLength;
        commandLine = commandLine + " -ss " + paddingSeconds + " -r "  + rate + geometry
                + " -an " + FileUtils.getSnapshotOutputFileStringTemplate(request, context);
        try {
            ExternalJobRunner.runClipperCommand(timeout, commandLine);
        } catch (ExternalProcessTimedOutException e) {
            logger.warn("Process '" + commandLine + "' timed out.");
            throw new ProcessorException(e);
        }

    }
}
