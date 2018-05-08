package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 *
 */
public class CentralWebserviceFactory {
    private static Logger logger = LoggerFactory.getLogger(CentralWebserviceFactory.class);


    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");


    private static void initialise(InfrastructureContext context) {
        if (context.getDomsApi() != null){
            return;
        }
        URL domsWSAPIEndpoint;
        try {
            domsWSAPIEndpoint = new URL(context.getDomsEndpoint());
            logger.debug("Initialising connection to doms at {}", context.getDomsEndpoint());
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL to DOMS not configured correctly. Was: " + context.getDomsEndpoint(), e);
        }
        CentralWebservice serviceInstance = new CentralWebserviceService(domsWSAPIEndpoint, CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();
        Map<String, Object> domsAPILogin = ((BindingProvider) serviceInstance).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, context.getDomsUsername());
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, context.getDomsPassword());
        context.setDomsApi(serviceInstance);
    }

    public static CentralWebservice getServiceInstance(InfrastructureContext context) {
        initialise(context);
        return context.getDomsApi();
    }

}
