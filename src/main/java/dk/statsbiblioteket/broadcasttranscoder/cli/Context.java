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

    private String domsEndpoint;

}
