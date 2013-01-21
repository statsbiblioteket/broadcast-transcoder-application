package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/26/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static boolean hasMediaOutputFile(TranscodeRequest request, Context context) {
        File dir = getFinalMediaOutputDir(request, context);
        if (!dir.exists()) {
            return false;
        }
        final String filenamePrefix = context.getProgrampid().replace("uuid:","");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(filenamePrefix);
            }
        };
        final File[] files = dir.listFiles(filter);
        if (files.length > 0) {
            logger.trace("Found output file " + files[0].getAbsolutePath());
        }
        return files.length > 0;
    }
    public static File findMediaOutputFile(TranscodeRequest request, Context context) {
        File dir = getFinalMediaOutputDir(request, context);
        if (!dir.exists()) {
            return null;
        }
        final String filenamePrefix = context.getProgrampid().replace("uuid:","");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(filenamePrefix);
            }
        };
        final File[] files = dir.listFiles(filter);
        if (files.length > 0) {
            logger.trace("Found output file " + files[0].getAbsolutePath());
            return files[0];
        }
        return null;
    }

    public static File getFinalMediaOutputFile(TranscodeRequest request, Context context) {
        File dir = getFinalMediaOutputDir(request, context);
        String filename = context.getProgrampid().replace("uuid:","");
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

    public static File getTemporaryMediaOutputFile(TranscodeRequest request, Context context) {
        File dir = getTemporaryMediaOutputDir(request, context);
        String filename = context.getProgrampid().replace("uuid:","");
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
    public static File getPreviewOutputFile(TranscodeRequest request, Context context) {
        File dir = getPreviewOutputDir(request, context);
        String filename = context.getProgrampid().replace("uuid:","");
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

    public static String getSnapshotOutputFileStringTemplate(TranscodeRequest request, Context context) {
        File dir = getSnapshotOutputDir(request, context);
        String name = context.getProgrampid().replace("uuid:","") + ".%d.png";
        return dir.getAbsolutePath() + "/" + name;
    }

    public static File getSnapshotOutputDir(TranscodeRequest request, Context context) {
            File rootDir = context.getSnapshotOutputRootdir();
            int depth = context.getFileDepth();
            logger.trace("Snapshot directory is relative to '" + rootDir + "'");
            return getOutputSubdirectory(context, rootDir, depth);
        }

    public static File getFinalMediaOutputDir(TranscodeRequest request, Context context) {
        File rootDir = context.getFileOutputRootdir();
        int depth = context.getFileDepth();
        logger.trace("Output directory is relative to '" + rootDir + "'");
        return getOutputSubdirectory(context, rootDir, depth);
    }

    public static File getTemporaryMediaOutputDir(TranscodeRequest request, Context context) {
        return new File(getFinalMediaOutputDir(request, context), "temp");
    }

    public static File getPreviewOutputDir(TranscodeRequest request, Context context) {
        File rootDir = context.getPreviewOutputRootdir();
        int depth = context.getFileDepth();
        logger.trace("Preview directory is relative to '" + rootDir + "'");
        return getOutputSubdirectory(context, rootDir, depth);
    }

    private static File getOutputSubdirectory(Context context, File rootDir, int depth) {
        String relativePath = "";
        String strippedPid = context.getProgrampid().replace("uuid:","");
        for (int pos = 0; pos < depth; pos++) {
            relativePath += strippedPid.charAt(pos) + "/";
        }
        logger.trace("Relative path is '" + relativePath + "'");
        return new File(rootDir, relativePath);
    }


    public static File getLockFile(TranscodeRequest request, Context context) {
        File rootDir = context.getLockDir();
        String basename = context.getProgrampid().replace("uuid:", "");
        return new File(rootDir, basename + ".lck");
    }
}
