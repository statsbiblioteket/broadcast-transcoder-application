package dk.statsbiblioteket.broadcasttranscoder.cli;

/**
 * Holder for parameters from the command line.
 */
public class Context {

    public String getDomsEndpoint() {
        return domsEndpoint;
    }

    public void setDomsEndpoint(String domsEndpoint) {
        this.domsEndpoint = domsEndpoint;
    }

    private String programpid;
    private String domsEndpoint;

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(int videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public String getDomsPassword() {
        return domsPassword;
    }

    public void setDomsPassword(String domsPassword) {
        this.domsPassword = domsPassword;
    }

    public String getDomsUsername() {
        return domsUsername;
    }

    public void setDomsUsername(String domsUsername) {
        this.domsUsername = domsUsername;
    }

    public String getProgrampid() {
        return programpid;
    }

    public void setProgrampid(String programpid) {
        this.programpid = programpid;
    }

    private String domsUsername;
    private String domsPassword;
    private int videoBitrate = 400;



}
