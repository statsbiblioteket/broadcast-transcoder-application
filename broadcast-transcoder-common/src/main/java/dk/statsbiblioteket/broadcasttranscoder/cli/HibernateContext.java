package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.TranscodingProcessInterface;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/30/13
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class HibernateContext<T> {

    private File hibernateConfigFile;

    private TranscodingProcessInterface<T> transcodingProcessInterface;

    public File getHibernateConfigFile() {
        return hibernateConfigFile;
    }

    public void setHibernateConfigFile(File hibernateConfigFile) {
        this.hibernateConfigFile = hibernateConfigFile;
    }

    public TranscodingProcessInterface<T> getTranscodingProcessInterface() {
        return transcodingProcessInterface;
    }

    public void setTranscodingProcessInterface(TranscodingProcessInterface<T> transcodingProcessInterface) {
        this.transcodingProcessInterface = transcodingProcessInterface;
    }
}
