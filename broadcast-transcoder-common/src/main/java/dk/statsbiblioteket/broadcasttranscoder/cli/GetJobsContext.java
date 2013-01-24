package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingState;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/23/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetJobsContext extends InfrastructureContext{

    private String domsViewAngle;

    private long fromTimestamp;

    private TranscodingState state;


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

    public TranscodingState getState() {
        return state;
    }

    public void setState(TranscodingState state) {
        this.state = state;
    }
}
