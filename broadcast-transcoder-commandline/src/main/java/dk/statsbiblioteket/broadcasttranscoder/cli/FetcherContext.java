package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.cli.GetJobsContext;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class FetcherContext<T> extends GetJobsContext<T> {


    private String viewAngle;
    private String collection;
    private String fedoraState;
    private int batchSize;

    private long since;
    private File fetcherFile;

    public void setViewAngle(String viewAngle) {
        this.viewAngle = viewAngle;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }


    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getViewAngle() {
        return viewAngle;
    }

    public String getCollection() {
        return collection;
    }

    public int getBatchSize() {
        return batchSize;
    }


    public long getSince() {
        return since;
    }

    public void setSince(long since) {
        this.since = since;
    }

    public String getFedoraState() {
        return fedoraState;
    }

    public void setFedoraState(String fedoraState) {
        this.fedoraState = fedoraState;
    }

    public File getFetcherFile() {
        return fetcherFile;
    }

    public void setFetcherFile(File fetcherFile) {
        this.fetcherFile = fetcherFile;
    }
}
