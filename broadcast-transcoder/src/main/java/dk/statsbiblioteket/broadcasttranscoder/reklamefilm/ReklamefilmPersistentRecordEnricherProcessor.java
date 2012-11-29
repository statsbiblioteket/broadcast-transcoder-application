package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.ReklamefileTranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.ReklamefilmTranscodingRecordDAO;

/**
 *
 */
public class ReklamefilmPersistentRecordEnricherProcessor extends ProcessorChainElement {
    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
        ReklamefilmTranscodingRecordDAO reklamefilmTranscodingRecordDAO = new ReklamefilmTranscodingRecordDAO(util);
        ReklamefileTranscodingRecord record = reklamefilmTranscodingRecordDAO.read(context.getProgrampid());
        record.setInputFile(request.getClipperCommand());
        record.setTranscodingCommand(request.getTranscoderCommand());
        reklamefilmTranscodingRecordDAO.update(record);
    }
}
