package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramStructure;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;

import java.io.File;
import java.util.List;
import java.util.Map;

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

    public ProgramStructure getProgramStructure() {
        return programStructure;
    }

    public void setProgramStructure(ProgramStructure programStructure) {
        this.programStructure = programStructure;
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

    private ProgramBroadcast programBroadcast;
    private ProgramStructure programStructure;
    private List<BroadcastMetadata> broadcastMetadata;
    private Map<BroadcastMetadata, File> fileMap;
    private long bitrate; // bytes/second
    private FileFormatEnum fileFormat;
    private List<FileClip> clips;


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
                      '}';
          }
      }


}
