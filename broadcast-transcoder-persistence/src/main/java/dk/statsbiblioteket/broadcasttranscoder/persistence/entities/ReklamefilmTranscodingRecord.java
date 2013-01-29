package dk.statsbiblioteket.broadcasttranscoder.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

/**
 * Persistent entity recording information about a successful transcoding.
 */
@Entity
public class ReklamefilmTranscodingRecord extends TranscodingRecord{

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }



    @Lob
    public String getTranscodingCommand() {
        return transcodingCommand;
    }

    public void setTranscodingCommand(String transcodingCommand) {
        this.transcodingCommand = transcodingCommand;
    }

    private String inputFile;
    private String transcodingCommand;



}
