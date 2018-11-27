package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.ThumbnailExtractionRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.ThumbnailExtractionRecord;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalProcessTimedOutException;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import org.apache.commons.lang.StringUtils;
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
 ffmpeg -i ./f/6/f/d/f6fdec8f-bde5-4795-8688-f3d5f8a1d540.mp4 -ss 120 -r 5/1557-an -s 312x234
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
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {
        if (request.isVideo()) { //Do not attempt snapshot extraction of non-video....
            HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
            ThumbnailExtractionRecordDAO dao = new ThumbnailExtractionRecordDAO(util);
            ThumbnailExtractionRecord record = dao.readOrCreate(request.getObjectPid());
            try {
                doExtraction(request, context, record);
            } finally {
                dao.update(record);
            }
        }
    }

    private void doExtraction(TranscodeRequest request, SingleTranscodingContext context, ThumbnailExtractionRecord record) throws ProcessorException {
        String commandline = getCommandLine(request, context);
    
        int length;
        //This if/else looks screwy but works so long as we use ffprobeDurationSeconds only for
        //reklamfilmer/snapshot-recodings where there is only a single input file.
        if (request.getFfprobeDurationSeconds() != null) {
            length = Math.round(request.getFfprobeDurationSeconds());
        } else {
            length = (int) (MetadataUtils.findProgramLengthMillis(request)/1000L);
        }
        final long timeout = (long) (1000.0 * length / context.getSnapshotTimeoutDivisor());
        try {
            process(record, commandline, timeout);
        } catch (ExternalProcessTimedOutException e) {
            String message = "Process '" + commandline + "' timed out after " + timeout + "ms.";
            logger.warn(message);
            record.setErrorMessage(message);
            record.setExtractionState(TranscodingStateEnum.FAILED);
            throw new ProcessorException("Process Timed out for " + request.getObjectPid() + " after " + timeout + "ms.",e);
        }
    
    
//        // This is a dirty fix because ffmpeg generates 2 snapshots close together at the start
//        String fileTemplate = FileUtils.getSnapshotOutputFileStringTemplate(request, context);
//        String firstFile = fileTemplate.replace("%d", "1");
//        try {
//            (new File(firstFile)).delete();
//        } catch (Exception e) {
//            logger.warn("Could not delete " + firstFile);
//        }
    }
    
    protected String getCommandLine(TranscodeRequest request, SingleTranscodingContext context) {
        int targetNumerator = context.getSnapshotTargetNumerator();
        int targetDenominator = context.getSnapshotTargetDenominator();
        int scale = context.getSnapshotScale();
        int paddingSeconds = context.getSnapshotPaddingSeconds();
        int nframes = context.getSnapshotFrames();
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

        return createCommandLineProperties(context.getSnapshotExtractorCommand(),
                targetNumerator,
                targetDenominator,
                scale,
                paddingSeconds,
                nframes,
                fullMediaFile,
                length,
                FileUtils.getSnapshotOutputFileStringTemplate(request, context),
                request.getDisplayAspectRatio());
    }
    
    private void process(ThumbnailExtractionRecord record,
                         String commandline,
                         long timeout) throws ProcessorException, ExternalProcessTimedOutException {
        record.setExtractionCommand(commandline);
        ExternalJobRunner.runClipperCommand(timeout, commandline);
        record.setExtractionState(TranscodingStateEnum.COMPLETE);
    }
    
    protected String createCommandLineProperties(String command,
                                                 int targetNumerator,
                                                 int targetDenominator,
                                                 int scale,
                                                 int paddingSeconds,
                                                 int nframes,
                                                 File fullMediaFile,
                                                 int length,
                                                 String snapshotOutputFileStringTemplate,
                                                 double input_scale) {
        return calculateVideoFilterArguments(command
                                                     .replace("$$START_OFFSET$$", paddingSeconds + "")
                                                     .replace("$$INPUT_FILES$$", " -i " + fullMediaFile + " ")
                                                     .replace("$$NFRAMES$$", nframes + "")
                                                     .replace("$$LENGTH$$", length + "")
                                                     .replace("$$HEIGHT$$", targetNumerator * scale + "")
                                                     .replace("$$WIDTH$$", targetDenominator * scale + "")
                                                     .replace("$$OUTPUT_FILE$$", snapshotOutputFileStringTemplate)
                , input_scale);
    }
    
    
    protected String createCommandLineKUANA(int targetNumerator,
                                            int targetDenominator,
                                            int scale,
                                            int paddingSeconds,
                                            int nframes,
                                            File fullMediaFile,
                                            int length,
                                            String snapshotOutputFileStringTemplate,
                                            double input_scale) {
        String commandline = "";

        //TODO this command line in config
        commandline += "ffmpeg";
        commandline += " -i " + fullMediaFile;
        commandline += " -ss " + paddingSeconds;


        commandline += " -t " + length;


        commandline += " -vf " + calculateVideoFilterArguments(
                "\"fps="+nframes+"/"+length+",scale=[WIDTH]x[HEIGHT],pad="+targetNumerator*scale+":"+targetDenominator*scale+":[PADDING_X]:[PADDING_Y]\"",
                input_scale
        );
        commandline += " -an";
        commandline += " -vframes "+nframes;
        commandline += " -y";
        commandline += " " + snapshotOutputFileStringTemplate;
        return commandline;
    }

    /**
     * Calculate filter arguments.
     * Expects a template looking like the following: fps=5/[LENGTH],scale=[WIDTH]x[HEIGHT],pad=416:234:[PADDING_X]:[PADDING_Y]
     * That example will generate 5 snapshots of size 416x234.
     * @param videoFilterTemplate Template for the filter arguments
     * @param inputVideoAspectRatio
     * @return The calculated filter arguments, ready for the executor.
     */
    protected String calculateVideoFilterArguments(String videoFilterTemplate, double inputVideoAspectRatio) {
        String[] paddingFilter = StringUtils.substringAfter(videoFilterTemplate, "pad=").split(":");
        if(paddingFilter.length < 2){
            throw new RuntimeException("pad filter not found for Video Filter option '" + videoFilterTemplate + "' on Snapshot Generator registry tool");
        }

        int wantedWidth, wantedHeight;
        try {
            wantedWidth = Integer.parseInt(paddingFilter[0]);
            wantedHeight = Integer.parseInt(paddingFilter[1]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to read width and height from pad filter tool option " + videoFilterTemplate + " on Snapshot Generator registry tool", e);
        }
        double wantedAspectRatio = (double) wantedWidth/wantedHeight;

        int videoWidth, videoHeight;
        int paddingX, paddingY;

        if (Math.abs(inputVideoAspectRatio - wantedAspectRatio) < 0.01) {
            videoWidth = wantedWidth;
            videoHeight = wantedHeight;
            paddingX = paddingY = 0;

        } else if (inputVideoAspectRatio - wantedAspectRatio < 0) {
            videoWidth = (int) Math.round(inputVideoAspectRatio * wantedHeight);
            videoHeight = wantedHeight;
            paddingX = (wantedWidth - videoWidth)/2;
            paddingY = 0;

        } else { // if (inputVideoAspectRatio - wantedAspectRatio > 0)
            videoWidth = wantedWidth;
            videoHeight = (int) Math.round(wantedWidth/inputVideoAspectRatio);
            paddingX = 0;
            paddingY = (wantedHeight - videoHeight)/2;
        }

        return videoFilterTemplate
                .replace("[WIDTH]", String.valueOf(videoWidth))
                .replace("[HEIGHT]", String.valueOf(videoHeight))
                .replace("[PADDING_X]", String.valueOf(paddingX))
                .replace("[PADDING_Y]", String.valueOf(paddingY));

    }

    /**
     * This is the best way to generate the stills, that ABR have found. It should be contrasted with
     * @see #createCommandLineKUANA(int, int, int, int, int, File, int, String, double) which should generate the very
     * same still images, but with more java code
     * @param numerator wanted height = 16
     * @param denominator wanted width = 9
     * @param scale scale from aspect above to actual size = 26
     * @param paddingSeconds padding Seconds
     * @param nframes number of stills
     * @param fullMediaFile the source media file
     * @param length the length of the file
     * @param snapshotOutputFileStringTemplate the template for the output files
     * @param input_scale the aspect ratio of the source file
     * @return
     */
    protected String createCommandline(int numerator,
                                       int denominator,
                                       int scale,
                                       int paddingSeconds,
                                       int nframes,
                                       File fullMediaFile,
                                       int length,
                                       String snapshotOutputFileStringTemplate,
                                       double input_scale) {
        //TODO this command line in config
        String commandLine = "ffmpeg -i " + fullMediaFile.getAbsolutePath();

        logger.debug("Creating snapshot for video with aspect ratio ("+numerator+"/"+denominator+") = ("+ numerator * scale +"/"+ denominator * scale +")");

        if (length < 3*paddingSeconds) {   //quick fix for very short programs
            paddingSeconds = 0;
        }
        String rate = nframes + "/" + length;

        //        From https://lists.ffmpeg.org/pipermail/ffmpeg-user/2011-July/001746.html
        //First we set the resolution
        String geometry = " -s " + numerator * scale + "x" + denominator * scale + " ";
        //Then the magic filter that scales and pads and everything
        geometry += " -vf \"fps="+rate +", scale=iw*sar:ih, pad=max(iw\\,ih*("+numerator+"/"+denominator+")):ow/("+numerator+"/"+denominator+"):(ow-iw)/2:(oh-ih)/2\" ";
        //And then we state the aspect ratio again
        geometry +=" -aspect "+numerator+":"+denominator+" ";

        commandLine = commandLine + " -ss " + paddingSeconds + " -t " + length + geometry
                + " -an -vframes " + (nframes) + " -y " + snapshotOutputFileStringTemplate;
        return commandLine;
    }
    
    
    protected String createCommandlineOld(int numerator,
                                          int denominator,
                                          int scale,
                                          int paddingSeconds,
                                          int nframes,
                                          File fullMediaFile,
                                          int length,
                                          String snapshotOutputFileStringTemplate,
                                          double aspectRatio) {
        double targetAspectRatio = (numerator*1.)/(denominator*1.);

        String commandLine = "ffmpeg -i " + fullMediaFile.getAbsolutePath();
        int N = numerator * scale;
        int M = denominator * scale;
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

        if (length < 3*paddingSeconds) {   //quick fix for very short programs
            paddingSeconds = 0;
        }
        int modLength = length - 2*paddingSeconds;
        String rate = (nframes - 1) + "/" + modLength;
        commandLine = commandLine + " -ss " + paddingSeconds + " -t " + modLength + " -r "  + rate + geometry
                + " -an -vframes " + (nframes + 1) + " -y " + snapshotOutputFileStringTemplate;
        return commandLine;

    }

}
