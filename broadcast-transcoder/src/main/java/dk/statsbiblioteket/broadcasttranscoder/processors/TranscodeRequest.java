package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramStructure;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public ProgramStructure getDomsProgramStructure() {
        return domsProgramStructure;
    }

    public void setDomsProgramStructure(ProgramStructure domsProgramStructure) {
        this.domsProgramStructure = domsProgramStructure;
    }

    public List<BroadcastMetadata> getBroadcastMetadata() {
        return broadcastMetadata;
    }

    public void setBroadcastMetadata(List<BroadcastMetadata> broadcastMetadata) {
        this.broadcastMetadata = broadcastMetadata;
    }

    public Map<BroadcastMetadata, File> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<BroadcastMetadata, File> fileMap) {
        this.fileMap = fileMap;
    }

    public long getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public FileFormatEnum getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(FileFormatEnum fileFormat) {
        this.fileFormat = fileFormat;
    }

    public List<FileClip> getClips() {
        return clips;
    }

    public void setClips(List<FileClip> clips) {
        this.clips = clips;
    }

    public String getDvbsubPid() {
        return dvbsubPid;
    }

    public void setDvbsubPid(String dvbsubPid) {
        this.dvbsubPid = dvbsubPid;
    }

    public String getVideoPid() {
        return videoPid;
    }

    public void setVideoPid(String videoPid) {
        this.videoPid = videoPid;
    }

    public String getVideoFcc() {
        return videoFcc;
    }

    public void setVideoFcc(String videoFcc) {
        this.videoFcc = videoFcc;
    }



    public String getAudioFcc() {
        return audioFcc;
    }

    public void setAudioFcc(String audioFcc) {
        this.audioFcc = audioFcc;
    }

    private Set<String> audioPids = new HashSet<String>();
    public void addAudioPid(String pid) {
        audioPids.add(pid);
    }

    public Set<String> getAudioPids() {
        return audioPids;
    }

    public int getMinimumAudioPid() {
        int minimum = Integer.MAX_VALUE;
        for (String pid: audioPids) {
            Integer newPid = Integer.decode(pid);
            minimum = Math.min(newPid, minimum);
        }
        return minimum;
    }

    public Double getDisplayAspectRatio() {
        return displayAspectRatio;
    }

    public void setDisplayAspectRatio(Double displayAspectRatio) {
        this.displayAspectRatio = displayAspectRatio;
    }

    public String getDisplayAspectRatioString() {
        return displayAspectRatioString;
    }

    public void setDisplayAspectRatioString(String displayAspectRatioString) {
        this.displayAspectRatioString = displayAspectRatioString;
    }

    public Map<String, BroadcastMetadata> getPidMap() {
        return pidMap;
    }

    public void setPidMap(Map<String, BroadcastMetadata> pidMap) {
        this.pidMap = pidMap;
    }

    public ProgramStructure getLocalProgramStructure() {
        return localProgramStructure;
    }

    public void setLocalProgramStructure(ProgramStructure localProgramStructure) {
        this.localProgramStructure = localProgramStructure;
    }

    public String getTranscoderCommand() {
        return transcoderCommand;
    }

    public void setTranscoderCommand(String transcoderCommand) {
        this.transcoderCommand = transcoderCommand;
    }

    public int getStartOffsetUsed() {
        return startOffsetUsed;
    }

    public void setStartOffsetUsed(int startOffsetUsed) {
        this.startOffsetUsed = startOffsetUsed;
    }

    public int getEndOffsetUsed() {
        return endOffsetUsed;
    }

    public void setEndOffsetUsed(int endOffsetUsed) {
        this.endOffsetUsed = endOffsetUsed;
    }

    public boolean isGoForTranscoding() {
        return goForTranscoding;
    }

    public void setGoForTranscoding(boolean goForTranscoding) {
        this.goForTranscoding = goForTranscoding;
    }

    public String getClipperCommand() {
        return clipperCommand;
    }

    public void setClipperCommand(String clipperCommand) {
        this.clipperCommand = clipperCommand;
    }

    public long getTimeoutMilliseconds() {
        return timeoutMilliseconds;
    }

    public void setTimeoutMilliseconds(long timeoutMilliseconds) {
        this.timeoutMilliseconds = timeoutMilliseconds;
    }

    public Float getFfprobeDurationSeconds() {
        return ffprobeDurationSeconds;
    }

    public void setFfprobeDurationSeconds(Float ffprobeDurationSeconds) {
        this.ffprobeDurationSeconds = ffprobeDurationSeconds;
    }

    public boolean isTvmeter() {
        return isTvmeter;
    }

    public void setTvmeter(boolean tvmeter) {
        isTvmeter = tvmeter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private ProgramBroadcast programBroadcast;
    private ProgramStructure domsProgramStructure;
    private ProgramStructure localProgramStructure;
    private List<BroadcastMetadata> broadcastMetadata;
    private Map<BroadcastMetadata, File> fileMap;
    private Map<String, BroadcastMetadata> pidMap;
    private long bitrate; // bytes/second
    private FileFormatEnum fileFormat;
    private List<FileClip> clips;

    private String dvbsubPid;
    private String videoPid;
    private String videoFcc;
    private String audioFcc;


    private Double displayAspectRatio;
    private String displayAspectRatioString;

    private String transcoderCommand;
    private int startOffsetUsed;
    private int endOffsetUsed;

    private String clipperCommand;

    private boolean goForTranscoding;

    private long timeoutMilliseconds;
    private Float ffprobeDurationSeconds;

    private boolean isTvmeter;
    private String title;

    private boolean isRejected = false;

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean rejected) {
        isRejected = rejected;
    }

    @Override
    public String toString() {
        return "TranscodeRequest{" +
                "audioPids=" + audioPids +
                ", programBroadcast=" + programBroadcast +
                ", domsProgramStructure=" + domsProgramStructure +
                ", localProgramStructure=" + localProgramStructure +
                ", broadcastMetadata=" + broadcastMetadata +
                ", fileMap=" + fileMap +
                ", pidMap=" + pidMap +
                ", bitrate=" + bitrate +
                ", fileFormat=" + fileFormat +
                ", clips=" + clips +
                ", dvbsubPid='" + dvbsubPid + '\'' +
                ", videoPid='" + videoPid + '\'' +
                ", videoFcc='" + videoFcc + '\'' +
                ", audioFcc='" + audioFcc + '\'' +
                ", displayAspectRatio=" + displayAspectRatio +
                ", displayAspectRatioString='" + displayAspectRatioString + '\'' +
                ", transcoderCommand='" + transcoderCommand + '\'' +
                ", startOffsetUsed=" + startOffsetUsed +
                ", endOffsetUsed=" + endOffsetUsed +
                ", clipperCommand='" + clipperCommand + '\'' +
                ", goForTranscoding=" + goForTranscoding +
                ", timeoutMilliseconds=" + timeoutMilliseconds +
                ", ffprobeDurationSeconds=" + ffprobeDurationSeconds +
                ", isTvmeter=" + isTvmeter +
                ", title='" + title + '\'' +
                '}';
    }

    /**
       * Class representing the absolute minimum information needed to clip data from a file
       */
    public static class FileClip {
          private String filepath;
          private Integer programId; //non-null only for mux'es
          private Long startOffsetBytes;
          private Long clipLength;

          private Long fileStartTime;
          private Long fileEndTime;


          public Long getFileStartTime() {
              return fileStartTime;
          }

          public void setFileStartTime(Long fileStartTime) {
              this.fileStartTime = fileStartTime;
          }

          public Long getFileEndTime() {
              return fileEndTime;
          }

          public void setFileEndTime(Long fileEndTime) {
              this.fileEndTime = fileEndTime;
          }

          public FileClip(String filepath) {
              this.filepath = filepath;
          }

          public void setProgramId(Integer programId) {
              this.programId = programId;
          }

          public void setStartOffsetBytes(Long startOffsetBytes) {
              this.startOffsetBytes = startOffsetBytes;
          }

          public void setClipLength(Long clipLength) {
              this.clipLength = clipLength;
          }

          public String getFilepath() {
              return filepath;
          }

        public Integer getProgramId() {
            return programId;
        }

        public Long getStartOffsetBytes() {
            return startOffsetBytes;
        }

        public Long getClipLength() {
            return clipLength;
        }

        @Override
        public String toString() {
            return "FileClip{" +
                    "filepath='" + filepath + '\'' +
                    ", programId=" + programId +
                    ", startOffsetBytes=" + startOffsetBytes +
                    ", clipLength=" + clipLength +
                    ", fileStartTime=" + fileStartTime +
                    ", fileEndTime=" + fileEndTime +
                    '}';
        }
    }


}
