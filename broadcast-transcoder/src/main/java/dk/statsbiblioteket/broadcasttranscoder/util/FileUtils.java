package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/26/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static boolean hasFinalMediaOutputFile(TranscodeRequest request, InfrastructureContext context) {
       return hasMediaOutputFile(request, context, true);
    }

    public static boolean hasTemporarMediaOutputFile(TranscodeRequest request, InfrastructureContext context) {
       return hasMediaOutputFile(request, context, false);
    }

    public static File findFinalMediaOutputFile(TranscodeRequest request, SingleTranscodingContext context) {
        return findMediaOutputFile(request, context, true);
    }

    public static File findTemporaryMediaOutputFile(TranscodeRequest request, SingleTranscodingContext context) {
        return findMediaOutputFile(request, context, false);
    }


     private static boolean hasMediaOutputFile(TranscodeRequest request, InfrastructureContext context, boolean isFinalFile) {
         File dir;
        if (isFinalFile) {
           dir = getFinalMediaOutputDir(request, context);
        } else {
            dir = getTemporaryMediaOutputDir(request, context);
        }
        if (!dir.exists()) {
            return false;
        }
        String filenamePrefix = getOutputFileBasename(request);
        if (filenamePrefix == null || filenamePrefix.trim().equals("")) {
               filenamePrefix = request.getObjectPid().replace("uuid:","");
        }
        final String finalisedFilenamePrefix = filenamePrefix;
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(finalisedFilenamePrefix);
            }
        };
        final File[] files = dir.listFiles(filter);
        if (files.length > 0) {
            logger.trace("Found output file " + files[0].getAbsolutePath());
        }
        return files.length > 0;
    }


    private static File findMediaOutputFile(TranscodeRequest request, SingleTranscodingContext context, boolean isFinalFile) {
        File dir;
        if (isFinalFile) {
            dir = getFinalMediaOutputDir(request, context);
        } else {
            dir = getTemporaryMediaOutputDir(request, context);
        }
        if (!dir.exists()) {
            return null;
        }
        String filenamePrefix = getOutputFileBasename(request);
        if (filenamePrefix == null || filenamePrefix.trim().equals("")) {
            filenamePrefix = request.getObjectPid().replace("uuid:","");
        }
        final String finalisedFilenamePrefix = filenamePrefix;
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(finalisedFilenamePrefix);
            }
        };
        final File[] files = dir.listFiles(filter);
        if (files.length > 0) {
            logger.trace("Found output file " + files[0].getAbsolutePath());
            return files[0];
        }
        return null;
    }

    public static File getFinalMediaOutputFile(TranscodeRequest request, SingleTranscodingContext context) {
        File dir = getFinalMediaOutputDir(request, context);
        String filename = getOutputFilename(request, context);
        return new File(dir, filename);
    }

    public static File getTemporaryMediaOutputFile(TranscodeRequest request, SingleTranscodingContext context) {
        File dir = getTemporaryMediaOutputDir(request, context);
        String filename = getOutputFilename(request, context);
        return new File(dir, filename);
    }

    private static String getOutputFilename(TranscodeRequest request, SingleTranscodingContext context ) {
        String filename = getOutputFileBasename(request);
        switch (request.getFileFormat()) {
            case SINGLE_PROGRAM_AUDIO_TS:
                filename += ".mp3";
                break;
            case AUDIO_WAV:
                filename += ".mp3";
                break;
            default:
                filename += "." + context.getVideoOutputSuffix();
        }
        return filename;
    }

    private static String getOutputFileBasename(TranscodeRequest request) {
        String filename = request.getObjectPid().replace("uuid:","");
        if (request.getOutputBasename() != null && request.getOutputBasename().trim().length()>0) {
             filename = request.getOutputBasename().trim();
        }
        return filename;
    }

    public static File getPreviewOutputFile(TranscodeRequest request, InfrastructureContext context) {
        File dir = getPreviewOutputDir(request, context);
        String filename = request.getObjectPid().replace("uuid:","");
        switch (request.getFileFormat()) {
            case SINGLE_PROGRAM_AUDIO_TS:
                filename += ".mp3";
                break;
            case AUDIO_WAV:
                filename += ".mp3";
                break;
            default:
                filename += ".flv";
        }
        return new File(dir, filename);
    }

    public static String getSnapshotOutputFileStringTemplate(TranscodeRequest request, InfrastructureContext context) {
        File dir = getSnapshotOutputDir(request, context);
        String name = request.getObjectPid().replace("uuid:","") + ".%d.png";
        return dir.getAbsolutePath() + "/" + name;
    }

    public static File getSnapshotOutputDir(TranscodeRequest request, InfrastructureContext context) {
        File rootDir = context.getSnapshotOutputRootdir();
        int depth = context.getFileDepth();
        logger.trace("Snapshot directory is relative to '" + rootDir + "'");
        return getOutputSubdirectory(request, rootDir, depth);
    }

    public static File getFinalMediaOutputDir(TranscodeRequest request, InfrastructureContext context) {
        File rootDir = context.getFileOutputRootdir();
        int depth = context.getFileDepth();
        logger.trace("Output directory is relative to '" + rootDir + "'");
        return getOutputSubdirectory(request, rootDir, depth);
    }

    public static File getTemporaryMediaOutputDir(TranscodeRequest request, InfrastructureContext context) {
        return new File(getFinalMediaOutputDir(request, context), "temp");
    }

    public static File getPreviewOutputDir(TranscodeRequest request, InfrastructureContext context) {
        File rootDir = context.getPreviewOutputRootdir();
        int depth = context.getFileDepth();
        logger.trace("Preview directory is relative to '" + rootDir + "'");
        return getOutputSubdirectory(request, rootDir, depth);
    }

    private static File getOutputSubdirectory(TranscodeRequest request, File rootDir, int depth) {
        String relativePath = "";
        String strippedPid = request.getObjectPid().replace("uuid:","");
        for (int pos = 0; pos < depth; pos++) {
            relativePath += strippedPid.charAt(pos) + "/";
        }
        logger.trace("Relative path is '" + relativePath + "'");
        return new File(rootDir, relativePath);
    }

    public static void cleanupAllTempDirs(InfrastructureContext context) {
        File rootOutputDir = context.getFileOutputRootdir();
        if (!rootOutputDir.exists()) {
            logger.info("No output directory to clean up");
            return;
        }
        IOFileFilter tempdirFilter = new NameFileFilter("temp");
        //Collection<File> tempDirs =  org.apache.commons.io.FileUtils.listFiles(rootOutputDir, tempdirFilter, TrueFileFilter.INSTANCE);
        Collection<File> tempDirs =  org.apache.commons.io.FileUtils.listFilesAndDirs(
                rootOutputDir, FalseFileFilter.FALSE, tempdirFilter
        );
        tempDirs.remove(rootOutputDir);
        for (File tempdir: tempDirs) {
            logger.info("Cleaning up directory " + tempdir.getAbsolutePath());
            for (File content: tempdir.listFiles()) {
                try {
                    logger.info("Deleting " + content.getAbsolutePath());
                    content.delete();
                } catch (Exception e) {
                     logger.info("Error deleting " + content.getAbsolutePath(), e);
                }
            }
        }
    }

    public static void deleteAndLogFailedFile(File file, Exception cause) {
        if (file == null) {
            logger.error("Attempting to delete null file");
            return;
        } else {
            if (cause != null) {
                logger.info("Deleting failed file '" + file.getAbsolutePath() + "' of length " + file.length() + " bytes", cause);
            } else {
                logger.info("Deleting failed file '" + file.getAbsolutePath() + "' of length " + file.length() + " bytes");
            }
            if (!file.delete()) {
                logger.warn("Could not delete '" + file.getAbsolutePath() + "'");
            }
        }
    }

}
