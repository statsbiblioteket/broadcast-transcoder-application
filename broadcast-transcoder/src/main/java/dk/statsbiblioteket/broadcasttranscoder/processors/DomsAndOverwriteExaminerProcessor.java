package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.TranscodingProcessInterface;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import dk.statsbiblioteket.doms.central.*;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.Long;
import java.lang.String;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class implements the logic for deciding go/no-go for transcoding. The logic is as follows:
 * If no distribution copy exists: go
 * Check old transcoding timestamp in database and if none is found set an estimated value
 * Calculate program diff for old timestamp and command-line timestamp. If there is a signifcant
 * difference: go
 * otherwise: no-go
 */
public class DomsAndOverwriteExaminerProcessor extends ProcessorChainElement {

    private static final Logger logger = LoggerFactory.getLogger(DomsAndOverwriteExaminerProcessor.class);

    private TransformerFactory transFact = TransformerFactory.newInstance();


    public DomsAndOverwriteExaminerProcessor() {
    }

    public DomsAndOverwriteExaminerProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

    /**
     *
     * @param request
     * @param context
     * @throws ProcessorException
     */
    @Override
    public void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {

        final String pid = request.getObjectPid();


        if (FileUtils.hasFinalMediaOutputFile(request, context)) { //file exists
            if ( !context.isOverwrite()){
                logger.info("Context is no-overwrite and media file exists so no need to transcode for " + request.getObjectPid());
                request.setGoForTranscoding(false);
                return;
            }
        } else { //no file
            //skip the test if something have changed, we should definitely transcode
            logger.info("No existing media file was found, so transcoding will be instantiated for " + request.getObjectPid());
            request.setGoForTranscoding(true);
            return;
        }
        logger.debug("Media file exists, checking whether program record in DOMS has significant changes for " + request.getObjectPid());

        if (!context.isOnlyTranscodeChanges()){
            //if we do not bother to check if the change is major
            request.setGoForTranscoding(true);
            return;
        }

        boolean majorChange = checkDoms(context, pid);
        request.setGoForTranscoding(majorChange);
    }

    private boolean checkDoms( SingleTranscodingContext context, String pid) throws ProcessorException {
        long timeStampOfNewChange = context.getTranscodingTimestamp();
        logger.info("Transcode doms record for " + pid + " timestamp " + timeStampOfNewChange + "=" + new Date(timeStampOfNewChange));
        TranscodingProcessInterface persister = context.getTranscodingProcessInterface();
        Long oldTranscodingTimestamp = persister.getLatestTranscodingTimestamp(pid);
        if (oldTranscodingTimestamp == null) {
            logger.info("Using default transcoding timestamp for " + pid);
            oldTranscodingTimestamp = context.getDefaultTranscodingTimestamp();
        }
        logger.info("Previous transcoding timestamp for " + pid + " is " + oldTranscodingTimestamp + "=" + new Date(oldTranscodingTimestamp));

        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);

        ViewBundle bundle = null;
        try {
            bundle = doms.getViewBundle(pid, context.getDomsViewAngle());
        } catch (InvalidCredentialsException e) {
            throw new ProcessorException("Invalid credentials to get the object bundle for pid " +  pid, e);
        } catch (InvalidResourceException e) {
            throw new ProcessorException("object bundle for pid " +  pid + " does not exist in Doms", e);
        } catch (MethodFailedException e) {
            throw new ProcessorException("Doms failed for the object bundle for pid " +  pid, e);
        }


        String bundleString = bundle.getContents();
        String oldStructure;
        String newStructure;
        try {
            oldStructure = extractBTAstructure(killNewerVersions(bundleString, oldTranscodingTimestamp));
            newStructure = extractBTAstructure(killNewerVersions(bundleString, timeStampOfNewChange));
        } catch (TransformerException e) {
            throw new ProcessorException("Failed to extract the BTA structure from the object bundle for pid " +  pid, e);
        }

        XMLUnit.setIgnoreWhitespace(true);
        try {

            //TODO sort order of file objects?
            Diff smallDiff = new Diff(oldStructure, newStructure);
            if (smallDiff.similar()) {
                logger.info("Not retranscoding " + pid + " as no significant changes found.");
                return false;
            }
        } catch (SAXException e) {
            throw new ProcessorException("Failed to parse the BTA structure from the object bundle for pid " +  pid + " for comparison");
        } catch (IOException e) {
            throw new ProcessorException("Failed to parse the BTA structure from the object bundle for pid " +  pid + " for comparison");
        }
        logger.info("Retranscoding " + pid + " as significant changes found.");
        return true;

    }


    String extractBTAstructure(String bundleString) throws TransformerException {

        StringWriter resultWriter = new StringWriter();
        StreamResult transformResult = new StreamResult(resultWriter);
        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(
                        Thread
                                .currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("xslt/extractBTAstructure.xslt"));

        javax.xml.transform.Transformer trans =
                transFact.newTransformer(xsltSource);
        trans.transform(new StreamSource(new StringReader(bundleString)),
                transformResult);
        resultWriter.flush();
        return resultWriter.toString();

    }

    public String killNewerVersions(String bundleString, long timestamp) throws TransformerException {

        String timeString = getTimeString(timestamp);
        javax.xml.transform.Source xmlSource =
                new StreamSource(new StringReader(bundleString));

        javax.xml.transform.Source xsltSource1 =
                new StreamSource(
                        Thread
                                .currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("xslt/killNewerVersions.xslt"));

        StringWriter tempWriter = new StringWriter();
        StreamResult transformResult1 = new StreamResult(tempWriter);

        javax.xml.transform.Transformer trans1 =
                transFact.newTransformer(xsltSource1);

        trans1.setParameter("timestamp", timeString);
        trans1.transform(xmlSource, transformResult1);
        return transformResult1.getWriter().toString();
    }

    String getTimeString(long timestamp) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
        return format.format(new Date(timestamp));

    }
}
