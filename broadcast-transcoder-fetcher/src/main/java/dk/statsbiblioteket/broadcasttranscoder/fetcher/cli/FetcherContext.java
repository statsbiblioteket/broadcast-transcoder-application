package dk.statsbiblioteket.broadcasttranscoder.fetcher.cli;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/21/12
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class FetcherContext extends Context {


    private String viewAngle;
    private String collection;
    private String state;
    private int batchSize;

    private long since;

    public void setViewAngle(String viewAngle) {
        this.viewAngle = viewAngle;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getState() {
        return state;
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
}
