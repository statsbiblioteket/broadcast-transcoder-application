package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;

/**
 *
 */
@Entity
public class BroadcastTranscodingRecord {

    @Lob
    public String getTranscodingCommand() {
        return transcodingCommand;
    }

    public void setTranscodingCommand(String transcodingCommand) {
        this.transcodingCommand = transcodingCommand;
    }

    public boolean isTvmeter() {
        return isTvmeter;
    }

    public void setTvmeter(boolean tvmeter) {
        isTvmeter = tvmeter;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public Date getBroadtcastStartTime() {
        return broadtcastStartTime;
    }

    public void setBroadtcastStartTime(Date broadtcastStartTime) {
        this.broadtcastStartTime = broadtcastStartTime;
    }

    public Date getBroadcastEndTime() {
        return broadcastEndTime;
    }

    public void setBroadcastEndTime(Date broadcastEndTime) {
        this.broadcastEndTime = broadcastEndTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

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



       private String transcodingCommand;
       private boolean isTvmeter;
       private int startOffset;
       private int endOffset;
       private Date broadtcastStartTime;
       private Date broadcastEndTime;
       private String title;
       private String channel;

}
