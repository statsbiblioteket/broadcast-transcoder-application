package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
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

    public SnapshotExtractorProcessor() {
    }

    public SnapshotExtractorProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        int targetNumerator = context.getSnapshotTargetNumerator();
        int targetDenominator = context.getSnapshotTargetDenominator();
        int scale = context.getSnapshotScale();
        double targetAspectRatio = (targetNumerator*1.)/(targetDenominator*1.);
        int paddingSeconds = context.getSnapshotPaddingSeconds();
        int nframes = context.getSnapshotFrames();
        float timeoutDivisor = context.getSnapshotTimeoutDivisor();
        File fullMediaFile = FileUtils.getFinalMediaOutputFile(request, context);
        File snapshotOutputDir = FileUtils.getSnapshotOutputDir(request, context);
        snapshotOutputDir.mkdirs();
        String commandLine = "ffmpeg -i " + fullMediaFile.getAbsolutePath();
        Double aspectRatio = request.getDisplayAspectRatio();
        logger.debug("Creating snapshot for video with display aspect ratio '" + aspectRatio + "' for " + context.getProgrampid());
        int N = targetNumerator * scale;
        int M = targetDenominator * scale;
        logger.debug("Required aspect ratio is '" + N + "/" + M + "' for " + context.getProgrampid());
        String geometry = "";
        if (Math.abs(aspectRatio - targetAspectRatio) < 0.01) {
            geometry = " -s " + N + "x" + M;
        } else if (aspectRatio - targetAspectRatio < 0.01) {
            double delta = (N - aspectRatio * M)/2.;
            int Nprime = (int) Math.round(aspectRatio * M);
            geometry = " -s " + Nprime + "x" + M +
                    " -vf 'pad=" + N + ":" + M + ":" + delta + ":0:black' ";
        } else if (targetAspectRatio - aspectRatio < 0.01) {
            int Mprime = (int) Math.round(N/aspectRatio);
            int delta = (M - Mprime)/2;
            geometry = " -s " + N + "x" + Mprime +
                    " -vf 'pad="  + N + ":" + M + ":0:" + delta + ":black' ";
        }
        int length;
        if (request.getFfprobeDurationSeconds() != null) {
          length = Math.round(request.getFfprobeDurationSeconds());
        } else {
          length = (int) (MetadataUtils.findProgramLengthMillis(request)/1000L);
        }
        if (length < 3*paddingSeconds) {   //quick fix for very short programs
            paddingSeconds = 0;
        }
        int modLength = length - 2*paddingSeconds;
        String rate = (nframes - 1) + "/" + modLength;
        commandLine = commandLine + " -ss " + paddingSeconds + " -t " + modLength + " -r "  + rate + geometry
                + " -an -y -vframes " + (nframes + 1) + " " + FileUtils.getSnapshotOutputFileStringTemplate(request, context);
        final long timeout = (long) (1000.0 * (float) length / timeoutDivisor);
        try {
            ExternalJobRunner.runClipperCommand(timeout, commandLine);
        } catch (ExternalProcessTimedOutException e) {
            logger.warn("Process '" + commandLine + "' timed out after " + timeout + "ms.");
            throw new ProcessorException("Process Timed out for " + context.getProgrampid() + " after " + timeout + "ms.",e);
        }
        // This is a dirty fix because ffmpeg generates 2 snapshots close together at the start
        String fileTemplate = FileUtils.getSnapshotOutputFileStringTemplate(request, context);
        String firstFile = fileTemplate.replace("%d", "1");
        try {
            (new File(firstFile)).delete();
        } catch (Exception e) {
            logger.warn("Could not delete " + firstFile);
        }
    }

}
