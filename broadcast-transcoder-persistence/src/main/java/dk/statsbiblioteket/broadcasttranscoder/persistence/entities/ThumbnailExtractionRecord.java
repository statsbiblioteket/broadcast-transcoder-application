package dk.statsbiblioteket.broadcasttranscoder.persistence.entities;

import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;

/**
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ThumbnailExtractionRecord extends Identifiable<String> {

    TranscodingStateEnum extractionState;
    String extractionCommand;
    String errorMessage;

    public TranscodingStateEnum getExtractionState() {
        return extractionState;
    }

    public void setExtractionState(TranscodingStateEnum extractionState) {
        this.extractionState = extractionState;
    }

    @Lob
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Lob
    public String getExtractionCommand() {
        return extractionCommand;
    }

    public void setExtractionCommand(String extractionCommand) {
        this.extractionCommand = extractionCommand;
    }
}
