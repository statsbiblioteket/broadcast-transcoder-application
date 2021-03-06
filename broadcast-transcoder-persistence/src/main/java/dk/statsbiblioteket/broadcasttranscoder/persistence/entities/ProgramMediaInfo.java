package dk.statsbiblioteket.broadcasttranscoder.persistence.entities;

import dk.statsbiblioteket.broadcasttranscoder.persistence.BroadcastTypeEnum;
import dk.statsbiblioteket.broadcasttranscoder.persistence.MediaTypeEnum;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ProgramMediaInfo extends Identifiable<Long>{


   private Date lastTouched;
    private String shardUuid;

    private boolean fileExists;
    private MediaTypeEnum mediaType;
    private BroadcastTypeEnum broadcastType;
    private long fileSizeByte;
    private Date fileTimestamp;
    private int startOffset;
    private int endOffset;
    private int lengthInSeconds;
    private long expectedFileSizeByte;
    private String transcodeCommandLine;
    private String note;



    public Date getLastTouched() {
        return lastTouched;
    }
    public void setLastTouched(Date lastTouched) {
        this.lastTouched = lastTouched;
    }
    public String getShardUuid() {
        return shardUuid;
    }
    public void setShardUuid(String shardUuid) {
        this.shardUuid = shardUuid;
    }
    public boolean isFileExists() {
        return fileExists;
    }
    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }
    public MediaTypeEnum getMediaType() {
        return mediaType;
    }
    public void setMediaType(MediaTypeEnum mediaType) {
        this.mediaType = mediaType;
    }
    public BroadcastTypeEnum getBroadcastType() {
        return broadcastType;
    }
    public void setBroadcastType(BroadcastTypeEnum broadcastType) {
        this.broadcastType = broadcastType;
    }
    public long getFileSizeByte() {
        return fileSizeByte;
    }
    public void setFileSizeByte(long fileSizeByte) {
        this.fileSizeByte = fileSizeByte;
    }
    public Date getFileTimestamp() {
        return fileTimestamp;
    }
    public void setFileTimestamp(Date fileTimestamp) {
        this.fileTimestamp = fileTimestamp;
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
    public int getLengthInSeconds() {
        return lengthInSeconds;
    }
    public void setLengthInSeconds(int lengthInSeconds) {
        this.lengthInSeconds = lengthInSeconds;
    }
    public long getExpectedFileSizeByte() {
        return expectedFileSizeByte;
    }
    public void setExpectedFileSizeByte(long expectedFileSizeByte) {
        this.expectedFileSizeByte = expectedFileSizeByte;
    }
    public String getTranscodeCommandLine() {
        return transcodeCommandLine;
    }
    public void setTranscodeCommandLine(String transcodeCommandLine) {
        this.transcodeCommandLine = transcodeCommandLine;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    @Override
    public String toString() {
        return "ProgramMediaInfo [lastTouched=" + lastTouched
                + ", shardUuid=" + shardUuid + ", fileExists=" + fileExists
                + ", mediaType=" + mediaType + ", broadcastType="
                + broadcastType + ", fileSizeByte=" + fileSizeByte
                + ", fileTimestamp=" + fileTimestamp + ", startOffset="
                + startOffset + ", endOffset=" + endOffset
                + ", lengthInSeconds=" + lengthInSeconds
                + ", expectedFileSizeByte=" + expectedFileSizeByte
                + ", transcodeCommandLine=" + transcodeCommandLine + ", note="
                + note + "]";
    }


}
