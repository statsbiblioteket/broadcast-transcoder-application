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
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.ProgramMediaInfoDAO;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.BroadcastTypeEnum;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.db.PreviewMediaInfo;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.extraction.model.MediaTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

public class PreviewClipperProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(PreviewClipperProcessor.class);


    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        long programLength = MetadataUtils.findProgramLengthMillis(request);
        File sourceFile = FileUtils.getMediaOutputFile(request, context);
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
            throw new ProcessorException(e);
        }
        persist(request, context, command);
        switch (request.getFileFormat()) {
            case MULTI_PROGRAM_MUX:
                this.setChildElement(new SnapshotExtractorProcessor());
                break;
            case MPEG_PS:
                this.setChildElement(new SnapshotExtractorProcessor());
                break;
            case SINGLE_PROGRAM_VIDEO_TS:
                this.setChildElement(new SnapshotExtractorProcessor());
                break;
            default:
                break;
        }
    }

    private void persist(TranscodeRequest request, Context context, String commandLine) {
        PreviewMediaInfo info = new PreviewMediaInfo();
        File outputFile =  FileUtils.getPreviewOutputDir(request, context);
        switch (request.getFileFormat()) {
                    case MULTI_PROGRAM_MUX:
                        info.setBroadcastType(BroadcastTypeEnum.TV);
                        break;
                    case MPEG_PS:
                        info.setBroadcastType(BroadcastTypeEnum.TV);
                        break;
                    case SINGLE_PROGRAM_VIDEO_TS:
                         info.setBroadcastType(BroadcastTypeEnum.TV);
                        break;
                    case AUDIO_WAV:
                         info.setBroadcastType(BroadcastTypeEnum.RADIO);
                        break;
                    case SINGLE_PROGRAM_AUDIO_TS:
                          info.setBroadcastType(BroadcastTypeEnum.RADIO);
                        break;
                }
        info.setEndOffset(request.getEndOffsetUsed());
        info.setExpectedFileSizeByte(context.getPreviewLength()*request.getBitrate());
        info.setFileExists(outputFile.exists());
        info.setFileSizeByte(outputFile.length());
        info.setFileTimestamp(new Date(outputFile.lastModified()));
        info.setLastTouched(new Date());
        info.setLengthInSeconds(context.getPreviewLength());
        if (outputFile.getAbsolutePath().endsWith("mp3")) {
                    info.setMediaType(MediaTypeEnum.MP3);
                } else if (outputFile.getAbsolutePath().endsWith("flv")) {
                    info.setMediaType(MediaTypeEnum.FLV);
                }
        info.setNote("shardUuid is programUuid");
        info.setTranscodeCommandLine(commandLine);
        new PreviewMediaInfoDAO(HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath())).create(info);

    }

}
