package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Persistent entity recording information about a successful transcoding.
 */
@Entity
public class ReklamefileTranscodingRecord {

    //TODO add any other fields we need to persist, starting with the transcoding timestamp

    @Id
    public String getDomsPid() {
        return domsPid;
    }

    public void setDomsPid(String domsPid) {
        this.domsPid = domsPid;
    }

    private String domsPid;
}
