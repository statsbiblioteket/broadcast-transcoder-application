package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Persistent entity recording information about a successful transcoding.
 */
@Entity
public class ReklamefileTranscodingRecord {

    @Id
    public String getDomsPid() {
        return domsPid;
    }

    public void setDomsPid(String domsPid) {
        this.domsPid = domsPid;
    }

    public Long getTranscodingTimestamp() {
        return transcodingTimestamp;
    }

    public void setTranscodingTimestamp(Long transcodingTimestamp) {
        this.transcodingTimestamp = transcodingTimestamp;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public Date getTranscodingDate() {
        return transcodingDate;
    }

    public void setTranscodingDate(Date transcodingDate) {
        this.transcodingDate = transcodingDate;
    }

    public String getTranscodingCommand() {
        return transcodingCommand;
    }

    public void setTranscodingCommand(String transcodingCommand) {
        this.transcodingCommand = transcodingCommand;
    }

    private String domsPid;
    private Long transcodingTimestamp;
    private Date transcodingDate;
    private String inputFile;
    private String transcodingCommand;

}
