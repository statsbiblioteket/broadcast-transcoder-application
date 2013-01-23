package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import javax.persistence.*;
import java.util.Date;

/**
 *  Persistent record detailing the state of a transcoding
 */
@Entity
public class BroadcastTranscodingRecord {


    private String domsProgramPid;
    private Long lastTranscodedTimestamp;
    private Long domsLatestTimestamp;

    @Enumerated(EnumType.STRING)
    private TranscodingState transcodingState;



    private String transcodingCommand;
    private boolean isTvmeter;
    private int startOffset;
    private int endOffset;
    private Date broadtcastStartTime;
    private Date broadcastEndTime;
    private String title;
    private String channel;
    private String failureMessage;


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


    public Long getDomsLatestTimestamp() {
        return domsLatestTimestamp;
    }

    public void setDomsLatestTimestamp(Long domsLatestTimestamp) {
        this.domsLatestTimestamp = domsLatestTimestamp;
    }

    public TranscodingState getTranscodingState() {
        return transcodingState;
    }

    public void setTranscodingState(TranscodingState transcodingState) {
        this.transcodingState = transcodingState;
    }

    @Override
    public String toString() {
        return "BroadcastTranscodingRecord{" +
                "domsProgramPid='" + domsProgramPid + '\'' +
                ", lastTranscodedTimestamp=" + lastTranscodedTimestamp +
                ", transcodingCommand='" + transcodingCommand + '\'' +
                ", isTvmeter=" + isTvmeter +
                ", startOffset=" + startOffset +
                ", endOffset=" + endOffset +
                ", broadtcastStartTime=" + broadtcastStartTime +
                ", broadcastEndTime=" + broadcastEndTime +
                ", title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BroadcastTranscodingRecord)) return false;

        BroadcastTranscodingRecord that = (BroadcastTranscodingRecord) o;

        if (endOffset != that.endOffset) return false;
        if (isTvmeter != that.isTvmeter) return false;
        if (startOffset != that.startOffset) return false;
        if (broadcastEndTime != null ? !broadcastEndTime.equals(that.broadcastEndTime) : that.broadcastEndTime != null)
            return false;
        if (broadtcastStartTime != null ? !broadtcastStartTime.equals(that.broadtcastStartTime) : that.broadtcastStartTime != null)
            return false;
        if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
        if (domsProgramPid != null ? !domsProgramPid.equals(that.domsProgramPid) : that.domsProgramPid != null)
            return false;
        if (lastTranscodedTimestamp != null ? !lastTranscodedTimestamp.equals(that.lastTranscodedTimestamp) : that.lastTranscodedTimestamp != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (transcodingCommand != null ? !transcodingCommand.equals(that.transcodingCommand) : that.transcodingCommand != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = domsProgramPid != null ? domsProgramPid.hashCode() : 0;
        result = 31 * result + (lastTranscodedTimestamp != null ? lastTranscodedTimestamp.hashCode() : 0);
        result = 31 * result + (transcodingCommand != null ? transcodingCommand.hashCode() : 0);
        result = 31 * result + (isTvmeter ? 1 : 0);
        result = 31 * result + startOffset;
        result = 31 * result + endOffset;
        result = 31 * result + (broadtcastStartTime != null ? broadtcastStartTime.hashCode() : 0);
        result = 31 * result + (broadcastEndTime != null ? broadcastEndTime.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        return result;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
