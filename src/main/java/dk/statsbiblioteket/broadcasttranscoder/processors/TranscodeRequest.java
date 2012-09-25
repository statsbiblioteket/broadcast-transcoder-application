package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramStructure;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/24/12
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TranscodeRequest {
    public ProgramBroadcast getProgramBroadcast() {
        return programBroadcast;
    }

    public void setProgramBroadcast(ProgramBroadcast programBroadcast) {
        this.programBroadcast = programBroadcast;
    }

    public ProgramStructure getProgramStructure() {
        return programStructure;
    }

    public void setProgramStructure(ProgramStructure programStructure) {
        this.programStructure = programStructure;
    }

    private ProgramBroadcast programBroadcast;
    private ProgramStructure programStructure;


}
