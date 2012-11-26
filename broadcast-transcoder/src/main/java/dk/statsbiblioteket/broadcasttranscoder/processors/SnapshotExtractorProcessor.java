package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.SnapshotMediaInfoDAO;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.db.SnapshotMediaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

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
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        int targetNumerator = context.getSnapshotTargetNumerator();
        int targetDenominator = context.getSnapshotTargetDenominator();
        int scale = context.getSnapshotScale();
        double targetAspectRatio = (targetNumerator*1.)/(targetDenominator*1.);
        int paddingSeconds = context.getSnapshotPaddingSeconds();
        int nframes = context.getSnapshotFrames();
        int timeoutDivisor = context.getSnapshotTimeoutDivisor();
        File fullMediaFile = FileUtils.getMediaOutputFile(request, context);
        File snapshotOutputDir = FileUtils.getSnapshotOutputDir(request, context);
        snapshotOutputDir.mkdirs();
        String commandLine = "ffmpeg -i " + fullMediaFile.getAbsolutePath();
        Double aspectRatio = request.getDisplayAspectRatio();
        logger.debug("Creating snapshot for video with display aspect ratio '" + aspectRatio + "'");
        int N = targetNumerator * scale;
        int M = targetDenominator * scale;
        logger.debug("Required aspect ratio is '" + N + "/" + M + "'");
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

        int length = (int) (MetadataUtils.findProgramLengthMillis(request)/1000L);
        int modLength = length - 2*paddingSeconds;
        String rate = nframes + "/" + modLength;
        commandLine = commandLine + " -ss " + paddingSeconds + " -t " + modLength + " -r "  + rate + geometry
                + " -an -y " + FileUtils.getSnapshotOutputFileStringTemplate(request, context);
        try {
            ExternalJobRunner.runClipperCommand(1000L*length/timeoutDivisor, commandLine);
        } catch (ExternalProcessTimedOutException e) {
            logger.warn("Process '" + commandLine + "' timed out.");
            throw new ProcessorException("Process Timed out for "+context.getProgrampid(),e);
        }
    }

}
