package dk.statsbiblioteket.broadcasttranscoder.fetcher;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.*;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.String;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Query the DOMS to check if the change is important enough to warrant a retranscoding
 */
public class DomsTranscodingStructureFetcher extends ProcessorChainElement {


    private TransformerFactory transFact = TransformerFactory.newInstance();


    public DomsTranscodingStructureFetcher() {
    }

    public DomsTranscodingStructureFetcher(ProcessorChainElement childElement) {
        super(childElement);
    }

    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {

        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);

        ViewBundle bundle = null;
        try {
            bundle = doms.getViewBundle(context.getProgrampid(), "BES");
        } catch (InvalidCredentialsException e) {
            throw new ProcessorException("Invalid credentials to get the object bundle for pid " + context.getProgrampid(), e);
        } catch (InvalidResourceException e) {
            throw new ProcessorException("object bundle for pid " + context.getProgrampid() + " does not exist in Doms", e);
        } catch (MethodFailedException e) {
            throw new ProcessorException("Doms failed for the object bundle for pid " + context.getProgrampid(), e);
        }

        //TODO timestamp
        long oldTranscodingTimestamp = context.getTimestampOfExistingTranscoding();
        if (oldTranscodingTimestamp < 0){//file doesnt exist, so check is redundant
            return;
        }
        long timeStampOfNewChange = context.getTimestampOfNewTranscoding();

        String bundleString = bundle.getContents();
        String oldStructure;
        String newStructure;
        try {
            oldStructure = extractBTAstructure(killNewerVersions(bundleString, oldTranscodingTimestamp));
            newStructure = extractBTAstructure(killNewerVersions(bundleString, timeStampOfNewChange));
        } catch (TransformerException e) {
            throw new ProcessorException("Failed to extract the BTA structure from the object bundle for pid " + context.getProgrampid(), e);
        }

        XMLUnit.setIgnoreWhitespace(true);
        try {

            //TODO sort order of file objects?
            Diff smallDiff = new Diff(oldStructure, newStructure);
            if (smallDiff.similar()){
                //Kill the transcoding chain
                setChildElement(null);
            }
        } catch (SAXException e) {
            throw new ProcessorException("Failed to parse the BTA structure from the object bundle for pid " + context.getProgrampid() + " for comparison");
        } catch (IOException e) {
            throw new ProcessorException("Failed to parse the BTA structure from the object bundle for pid " + context.getProgrampid() + " for comparison");
        }

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

    String killNewerVersions(String bundleString, long timestamp) throws TransformerException {

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
