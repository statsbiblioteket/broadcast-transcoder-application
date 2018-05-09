package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.ThumbnailExtractionRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.ThumbnailExtractionRecord;
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
        HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
        ThumbnailExtractionRecordDAO dao = new ThumbnailExtractionRecordDAO(util);
        ThumbnailExtractionRecord record = dao.readOrCreate(request.getObjectPid());
        try {
            doExtraction(request, context, record);
        } finally {
            dao.update(record);
        }
    }

    private void doExtraction(TranscodeRequest request, SingleTranscodingContext context, ThumbnailExtractionRecord record) throws ProcessorException {
        int targetNumerator = context.getSnapshotTargetNumerator();
        int targetDenominator = context.getSnapshotTargetDenominator();
        int scale = context.getSnapshotScale();
        int paddingSeconds = context.getSnapshotPaddingSeconds();
        int nframes = context.getSnapshotFrames();
        float timeoutDivisor = context.getSnapshotTimeoutDivisor();
        File fullMediaFile = FileUtils.getFinalMediaOutputFile(request, context);
        File snapshotOutputDir = FileUtils.getSnapshotOutputDir(request, context);
        snapshotOutputDir.mkdirs();

        int length;
        //This if/else looks screwy but works so long as we use ffprobeDurationSeconds only for
        //reklamfilmer/snapshot-recodings where there is only a single input file.
        if (request.getFfprobeDurationSeconds() != null) {
          length = Math.round(request.getFfprobeDurationSeconds());
        } else {
          length = (int) (MetadataUtils.findProgramLengthMillis(request)/1000L);
        }

        String commandLine = createCommandline(
                targetNumerator,
                targetDenominator,
                scale,
                paddingSeconds,
                nframes,
                fullMediaFile,
                length,
                FileUtils.getSnapshotOutputFileStringTemplate(request, context));

        final long timeout = (long) (1000.0 * (float) length / timeoutDivisor);
        record.setExtractionCommand(commandLine);
        try {
            ExternalJobRunner.runClipperCommand(timeout, commandLine);
            record.setExtractionState(TranscodingStateEnum.COMPLETE);
        } catch (ExternalProcessTimedOutException e) {
            String message = "Process '" + commandLine + "' timed out after " + timeout + "ms.";
            logger.warn(message);
            record.setErrorMessage(message);
            record.setExtractionState(TranscodingStateEnum.FAILED);
            throw new ProcessorException("Process Timed out for " + request.getObjectPid() + " after " + timeout + "ms.",e);
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

    protected String createCommandline(int numerator, int denominator, int scale, int paddingSeconds, int nframes, File fullMediaFile, int length, String snapshotOutputFileStringTemplate) {
        String commandLine = "ffmpeg -i " + fullMediaFile.getAbsolutePath();

        int width = numerator * scale;
        int height = denominator * scale;
        logger.debug("Creating snapshot for video with aspect ratio ("+numerator+"/"+denominator+") = ("+width+"/"+height+")");

        //        From https://lists.ffmpeg.org/pipermail/ffmpeg-user/2011-July/001746.html
        //First we set the resolution
        String geometry = " -s " + width + "x" + height + " ";
        //Then the magic filter that scales and pads and everything
        geometry += " -vf \"scale=iw*sar:ih , pad=max(iw\\,ih*("+numerator+"/"+denominator+")):ow/("+numerator+"/"+denominator+"):(ow-iw)/2:(oh-ih)/2\" ";
        //And then we state the aspect ratio again
        geometry +=" -aspect "+numerator+":"+denominator+" ";

        if (length < 3*paddingSeconds) {   //quick fix for very short programs
            paddingSeconds = 0;
        }
        int modLength = length - 2*paddingSeconds;
        String rate = (nframes - 1) + "/" + modLength;
        commandLine = commandLine + " -ss " + paddingSeconds + " -t " + modLength + " -r "  + rate + geometry
                + " -an -y -vframes " + (nframes + 1) + " " + snapshotOutputFileStringTemplate;
        return commandLine;
    }

}
