package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.BroadcastTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.BroadcastTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 12/4/12
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class BroadcastTranscodingRecordEnricherProcessor extends ProcessorChainElement {
    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {
        HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
        BroadcastTranscodingRecordDAO dao = new BroadcastTranscodingRecordDAO(util);
        BroadcastTranscodingRecord record = dao.read(request.getObjectPid());
        if (record == null) {
            throw new ProcessorException("Attempted to enrich metadata for a non-existent database " +
                    "record for pid " + request.getObjectPid());
        }
        record.setTvmeter(request.isTvmeter());
        record.setBroadcastStartTime(MetadataUtils.getProgramStart(request));
        record.setBroadcastEndTime(MetadataUtils.getProgramEnd(request));
        record.setChannel(request.getProgramBroadcast().getChannelId());
        record.setVideo(request.isVideo());
        record.setEndOffset(request.getEndOffsetUsed());
        record.setStartOffset(request.getStartOffsetUsed());
        record.setTitle(request.getTitle());
        record.setTranscodingCommand(request.getTranscoderCommand());
        dao.update(record);
    }
}
