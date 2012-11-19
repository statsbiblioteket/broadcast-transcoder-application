package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.doms.central.CentralWebservice;

import java.io.File;

/**
 * Holder for parameters from the command line.
 */
public class Context {

    public String getProgrampid() {
        return programpid;
    }

    public void setProgrampid(String programpid) {
        this.programpid = programpid;
    }

    public File getInfrastructuralConfigFile() {
        return infrastructuralConfigFile;
    }

    public void setInfrastructuralConfigFile(File infrastructuralConfigFile) {
        this.infrastructuralConfigFile = infrastructuralConfigFile;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(int videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public String getDomsEndpoint() {
        return domsEndpoint;
    }

    public void setDomsEndpoint(String domsEndpoint) {
        this.domsEndpoint = domsEndpoint;
    }

    public String getDomsUsername() {
        return domsUsername;
    }

    public void setDomsUsername(String domsUsername) {
        this.domsUsername = domsUsername;
    }

    public String getDomsPassword() {
        return domsPassword;
    }

    public void setDomsPassword(String domsPassword) {
        this.domsPassword = domsPassword;
    }

    public File getFileOutputRootdir() {
        return fileOutputRootdir;
    }

    public void setFileOutputRootdir(File fileOutputRootdir) {
        this.fileOutputRootdir = fileOutputRootdir;
    }

    public File getPreviewOutputRootdir() {
        return previewOutputRootdir;
    }

    public void setPreviewOutputRootdir(File previewOutputRootdir) {
        this.previewOutputRootdir = previewOutputRootdir;
    }

    public File getSnapshotOutputRootdir() {
        return snapshotOutputRootdir;
    }

    public void setSnapshotOutputRootdir(File snapshotOutputRootdir) {
        this.snapshotOutputRootdir = snapshotOutputRootdir;
    }

    public File getLockDir() {
        return lockDir;
    }

    public void setLockDir(File lockDir) {
        this.lockDir = lockDir;
    }

    public int getFileDepth() {
        return fileDepth;
    }

    public void setFileDepth(int fileDepth) {
        this.fileDepth = fileDepth;
    }

    public int getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(int audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public String getX264VlcParams() {
        return x264VlcParams;
    }

    public void setX264VlcParams(String x264VlcParams) {
        this.x264VlcParams = x264VlcParams;
    }

    public int getTranscodingTimeoutDivisor() {
        return transcodingTimeoutDivisor;
    }

    public void setTranscodingTimeoutDivisor(int transcodingTimeoutDivisor) {
        this.transcodingTimeoutDivisor = transcodingTimeoutDivisor;
    }

    public long getAnalysisClipLength() {
        return analysisClipLength;
    }

    public void setAnalysisClipLength(long analysisClipLength) {
        this.analysisClipLength = analysisClipLength;
    }

    public int getStartOffsetTS() {
        return startOffsetTS;
    }

    public void setStartOffsetTS(int startOffsetTS) {
        this.startOffsetTS = startOffsetTS;
    }

    public int getEndOffsetTS() {
        return endOffsetTS;
    }

    public void setEndOffsetTS(int endOffsetTS) {
        this.endOffsetTS = endOffsetTS;
    }

    public int getStartOffsetPS() {
        return startOffsetPS;
    }

    public void setStartOffsetPS(int startOffsetPS) {
        this.startOffsetPS = startOffsetPS;
    }

    public int getEndOffsetPS() {
        return endOffsetPS;
    }

    public void setEndOffsetPS(int endOffsetPS) {
        this.endOffsetPS = endOffsetPS;
    }

    public int getStartOffsetWAV() {
        return startOffsetWAV;
    }

    public void setStartOffsetWAV(int startOffsetWAV) {
        this.startOffsetWAV = startOffsetWAV;
    }

    public int getEndOffsetWAV() {
        return endOffsetWAV;
    }

    public void setEndOffsetWAV(int endOffsetWAV) {
        this.endOffsetWAV = endOffsetWAV;
    }

    public String getFileFinderUrl() {
        return fileFinderUrl;
    }

    public void setFileFinderUrl(String fileFinderUrl) {
        this.fileFinderUrl = fileFinderUrl;
    }

    public int getMaxFilesFetched() {
        return maxFilesFetched;
    }

    public void setMaxFilesFetched(int maxFilesFetched) {
        this.maxFilesFetched = maxFilesFetched;
    }

    public int getMaxMissingStart() {
        return maxMissingStart;
    }

    public void setMaxMissingStart(int maxMissingStart) {
        this.maxMissingStart = maxMissingStart;
    }

    public int getMaxHole() {
        return maxHole;
    }

    public void setMaxHole(int maxHole) {
        this.maxHole = maxHole;
    }

    public int getMaxMissingEnd() {
        return maxMissingEnd;
    }

    public void setMaxMissingEnd(int maxMissingEnd) {
        this.maxMissingEnd = maxMissingEnd;
    }

    public File getHibernateConfigFile() {
        return hibernateConfigFile;
    }

    public void setHibernateConfigFile(File hibernateConfigFile) {
        this.hibernateConfigFile = hibernateConfigFile;
    }

    public File getBehaviourConfigFile() {
        return behaviourConfigFile;
    }

    public void setBehaviourConfigFile(File behaviourConfigFile) {
        this.behaviourConfigFile = behaviourConfigFile;
    }

    public int getGapToleranceSeconds() {
        return gapToleranceSeconds;
    }

    public void setGapToleranceSeconds(int gapToleranceSeconds) {
        this.gapToleranceSeconds = gapToleranceSeconds;
    }

    public int getPreviewLength() {
        return previewLength;
    }

    public void setPreviewLength(int previewLength) {
        this.previewLength = previewLength;
    }

    public int getPreviewTimeout() {
        return previewTimeout;
    }

    public void setPreviewTimeout(int previewTimeout) {
        this.previewTimeout = previewTimeout;
    }

    public String getX264FfmpegParams() {
        return x264FfmpegParams;
    }

    public void setX264FfmpegParams(String x264FfmpegParams) {
        this.x264FfmpegParams = x264FfmpegParams;
    }

    public int getSnapshotScale() {
        return snapshotScale;
    }

    public void setSnapshotScale(int snapshotScale) {
        this.snapshotScale = snapshotScale;
    }

    public int getSnapshotTargetNumerator() {
        return snapshotTargetNumerator;
    }

    public void setSnapshotTargetNumerator(int snapshotTargetNumerator) {
        this.snapshotTargetNumerator = snapshotTargetNumerator;
    }

    public int getSnapshotTargetDenominator() {
        return snapshotTargetDenominator;
    }

    public void setSnapshotTargetDenominator(int snapshotTargetDenominator) {
        this.snapshotTargetDenominator = snapshotTargetDenominator;
    }

    public int getSnapshotFrames() {
        return snapshotFrames;
    }

    public void setSnapshotFrames(int snapshotFrames) {
        this.snapshotFrames = snapshotFrames;
    }

    public int getSnapshotPaddingSeconds() {
        return snapshotPaddingSeconds;
    }

    public void setSnapshotPaddingSeconds(int snapshotPaddingSeconds) {
        this.snapshotPaddingSeconds = snapshotPaddingSeconds;
    }

    public int getSnapshotTimeoutDivisor() {
        return snapshotTimeoutDivisor;
    }

    public void setSnapshotTimeoutDivisor(int snapshotTimeoutDivisor) {
        this.snapshotTimeoutDivisor = snapshotTimeoutDivisor;
    }

    public String getSoxTranscodeParams() {
        return soxTranscodeParams;
    }

    public void setSoxTranscodeParams(String soxTranscodeParams) {
        this.soxTranscodeParams = soxTranscodeParams;
    }

    private String programpid;
    private File infrastructuralConfigFile;
    private File behaviourConfigFile;

    private String domsEndpoint;
    private String domsUsername;
    private String domsPassword;

    private File fileOutputRootdir;
    private File previewOutputRootdir;
    private File snapshotOutputRootdir;
    private File lockDir;
    private int fileDepth;

    private int videoBitrate;
    private int audioBitrate;
    private int videoHeight;
    private String x264VlcParams;
    private String x264FfmpegParams;
    private int transcodingTimeoutDivisor;
    private long analysisClipLength;

    private int maxMissingStart;
    private int maxMissingEnd;
    private int maxHole;
    private int gapToleranceSeconds;

    private int startOffsetTS;
    private int endOffsetTS;
    private int startOffsetPS;
    private int endOffsetPS;
    private int startOffsetWAV;
    private int endOffsetWAV;

    private String fileFinderUrl;
    private int maxFilesFetched;

    private File hibernateConfigFile;

    private int previewLength;
    private int previewTimeout;

    private int snapshotScale;
    private int snapshotTargetNumerator;
    private int snapshotTargetDenominator;
    private int snapshotFrames;
    private int snapshotPaddingSeconds;
    private int snapshotTimeoutDivisor;

    private String soxTranscodeParams;




    private CentralWebservice domsApi = null;

    public CentralWebservice getDomsApi() {
        return domsApi;
    }

    public void setDomsApi(CentralWebservice domsApi) {
        this.domsApi = domsApi;
    }
}
