package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 *
 */
public class CentralWebserviceFactory {
    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");
    private static CentralWebservice serviceInstance;
    private static CentralWebserviceFactory factoryInstance;

    private CentralWebservice getServiceInstance() {
        return serviceInstance;
    }

    private CentralWebserviceFactory(Context context) {
        URL domsWSAPIEndpoint;
        try {
            domsWSAPIEndpoint = new URL(context.getDomsEndpoint());
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL to DOMS not configured correctly. Was: " + context.getDomsEndpoint(), e);
        }
        serviceInstance = new CentralWebserviceService(domsWSAPIEndpoint, CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();
        Map<String, Object> domsAPILogin = ((BindingProvider) serviceInstance).getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, context.getDomsUsername());
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, context.getDomsPassword());
    }

    public static CentralWebservice getServiceInstance(Context context) {
       return getInstance(context).getServiceInstance();
    }

    private static synchronized CentralWebserviceFactory getInstance(Context context) {
        if (factoryInstance == null) {
            factoryInstance = new CentralWebserviceFactory(context);
        }
        return factoryInstance;
    }

}
