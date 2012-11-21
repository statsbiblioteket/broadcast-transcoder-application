package dk.statsbiblioteket.broadcasttranscoder.fetcher;

import dk.statsbiblioteket.broadcasttranscoder.fetcher.cli.FetcherContext;
import dk.statsbiblioteket.broadcasttranscoder.util.CentralWebserviceFactory;
import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.util.xml.DOM;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.String;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomsTranscodingStructureFetcher {


    public String processThis(FetcherContext context, RecordDescription recordDescription) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException, TransformerException {
        CentralWebservice doms = CentralWebserviceFactory.getServiceInstance(context);

        ViewBundle bundle = doms.getViewBundle(recordDescription.getPid(), context.getViewAngle());
        String bundleString = bundle.getContents();


        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(new StringReader(bundleString), "originalPBCore");
        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(
                        Thread
                                .currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("xslt/extractBTAstructure.xslt"));


        StringWriter resultWriter = new StringWriter();
        StreamResult transformResult = new StreamResult(resultWriter);


        TransformerFactory transFact = TransformerFactory.newInstance();


        javax.xml.transform.Transformer trans =
                transFact.newTransformer(xsltSource);
        trans.transform(xmlSource, transformResult);
        resultWriter.flush();
        return resultWriter.toString();
    }
}
