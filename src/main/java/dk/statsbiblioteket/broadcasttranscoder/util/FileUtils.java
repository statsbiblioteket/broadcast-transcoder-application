package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/26/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static File getMediaOutputFile(TranscodeRequest request, Context context) {
        File dir = getMediaOutputDir(request, context);
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

    public static File getMediaOutputDir(TranscodeRequest request, Context context) {
        File rootDir = context.getFileOutputRootdir();
        int depth = context.getFileDepth();
        logger.trace("Output directory is relative to '" + rootDir + "'");
        return getOutputSubdirectory(context, rootDir, depth);
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
