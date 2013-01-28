package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/23/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetJobsContext<T> extends InfrastructureContext<T>{

    private String domsViewAngle;

    private long fromTimestamp;

    private TranscodingStateEnum state;
    private String collection;


    public String getDomsViewAngle() {
        return domsViewAngle;
    }

    public void setDomsViewAngle(String domsViewAngle) {
        this.domsViewAngle = domsViewAngle;
    }

    public long getFromTimestamp() {
        return fromTimestamp;
    }

    public void setFromTimestamp(long fromTimestamp) {
        this.fromTimestamp = fromTimestamp;
    }

    public TranscodingStateEnum getState() {
        return state;
    }

    public void setState(TranscodingStateEnum state) {
        this.state = state;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getCollection() {
        return collection;
    }
}
