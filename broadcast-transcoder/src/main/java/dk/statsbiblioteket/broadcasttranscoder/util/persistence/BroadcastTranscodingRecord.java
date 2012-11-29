package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 */
@Entity
public class BroadcastTranscodingRecord {

    //TODO add things we need to persist, such as offsets
    @Id
    public String getDomsProgramPid() {
        return domsProgramPid;
    }

    public void setDomsProgramPid(String domsProgramPid) {
        this.domsProgramPid = domsProgramPid;
    }

    public Long getLastTranscodedTimestamp() {
        return lastTranscodedTimestamp;
    }

    public void setLastTranscodedTimestamp(Long lastTranscodedTimestamp) {
        this.lastTranscodedTimestamp = lastTranscodedTimestamp;
    }

    private String domsProgramPid;
    private Long lastTranscodedTimestamp;

}
