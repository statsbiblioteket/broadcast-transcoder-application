package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.ReklamefilmTranscodingRecordDAO;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.ReklamefilmTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;

/**
 *
 */
public class ReklamefilmPersistentRecordEnricherProcessor extends ProcessorChainElement {
    @Override
    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {
        HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
        ReklamefilmTranscodingRecordDAO reklamefilmTranscodingRecordDAO = new ReklamefilmTranscodingRecordDAO(util);
        ReklamefilmTranscodingRecord record = reklamefilmTranscodingRecordDAO.read(request.getObjectPid());
        record.setInputFile(request.getClipperCommand());
        record.setTranscodingCommand(request.getTranscoderCommand());
        reklamefilmTranscodingRecordDAO.update(record);
    }
}
