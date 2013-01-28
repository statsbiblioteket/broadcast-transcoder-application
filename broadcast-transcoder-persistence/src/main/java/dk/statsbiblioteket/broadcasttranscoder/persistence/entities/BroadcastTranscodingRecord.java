package dk.statsbiblioteket.broadcasttranscoder.persistence.entities;

import dk.statsbiblioteket.broadcasttranscoder.persistence.TranscodingStateEnum;

import javax.persistence.*;
import java.util.Date;

/**
 *  Persistent record detailing the state of a transcoding
 */
@Entity
public class BroadcastTranscodingRecord extends TranscodingRecord{

    private String transcodingCommand;
    private boolean isTvmeter;
    private int startOffset;
    private int endOffset;
    private Date broadtcastStartTime;
    private Date broadcastEndTime;
    private String title;
    private String channel;


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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BroadcastTranscodingRecord)) return false;
        if (!super.equals(o)) return false;

        BroadcastTranscodingRecord that = (BroadcastTranscodingRecord) o;

        if (endOffset != that.endOffset) return false;
        if (isTvmeter != that.isTvmeter) return false;
        if (startOffset != that.startOffset) return false;
        if (broadcastEndTime != null ? !broadcastEndTime.equals(that.broadcastEndTime) : that.broadcastEndTime != null)
            return false;
        if (broadtcastStartTime != null ? !broadtcastStartTime.equals(that.broadtcastStartTime) : that.broadtcastStartTime != null)
            return false;
        if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (transcodingCommand != null ? !transcodingCommand.equals(that.transcodingCommand) : that.transcodingCommand != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
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

    @Override
    public String toString() {
        return "BroadcastTranscodingRecord{" +
                super.toString() + "," +
                "transcodingCommand='" + transcodingCommand + '\'' +
                ", isTvmeter=" + isTvmeter +
                ", startOffset=" + startOffset +
                ", endOffset=" + endOffset +
                ", broadtcastStartTime=" + broadtcastStartTime +
                ", broadcastEndTime=" + broadcastEndTime +
                ", title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
