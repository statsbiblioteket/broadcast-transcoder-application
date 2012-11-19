package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.HibernateUtil;
import dk.statsbiblioteket.broadcasttranscoder.util.persistence.MetadataDAO;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.db.Metadata;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.extraction.DOMSMetadataExtractor;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.extraction.exception.DOMSMetadataExtractionParsePBCoreException;
import dk.statsbiblioteket.mediaplatform.bes.mediafilelog.batch.extraction.model.PBCoreProgramMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 10/1/12
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class PersistentMetadataExtractorProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(PersistentMetadataExtractorProcessor.class);


    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        Metadata metadata = getPersistentMedata(request, context);
        MetadataDAO dao = new MetadataDAO(HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath()));
        dao.create(metadata);


    }

    private Metadata getPersistentMedata (TranscodeRequest request, Context context) throws ProcessorException {
          Metadata metadata = new Metadata();
          CentralWebservice domsAPI = CentralWebserviceFactory.getServiceInstance(context);
          String structureXmlString = null;
          PBCoreProgramMetadata programMetadata =  null;
          try {
              structureXmlString = domsAPI.getDatastreamContents(context.getProgrampid(), "PBCORE");
          } catch (Exception e) {
              throw new ProcessorException(e);
          }
          try {
              programMetadata = DOMSMetadataExtractor.extractMetadataFromPBCore(null, structureXmlString);
          } catch (DOMSMetadataExtractionParsePBCoreException e) {
              throw new ProcessorException(e);
          }
          metadata.setProgramUuid(context.getProgrampid());
          metadata.setChannelID(programMetadata.channel);
          metadata.setSbChannelID("");
          metadata.setTvmeterStartTime(request.getProgramBroadcast().getTimeStart().toGregorianCalendar().getTime());
          metadata.setTvmeterEndTime(request.getProgramBroadcast().getTimeStop().toGregorianCalendar().getTime());
          metadata.setProgramTitle(programMetadata.titel);
          metadata.setRitzauStartTime(programMetadata.start);
          metadata.setRitzauEndTime(programMetadata.end);
          metadata.setLastChangedDate(new Date());
          metadata.setNote("Extracted with BroadcastTranscoderApplication .");
          return metadata;
      }
}
