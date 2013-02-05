package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.persistence.dao.TranscodingProcessInterface;
import dk.statsbiblioteket.doms.central.CentralWebservice;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/23/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class InfrastructureContext<T>  extends HibernateContext<T>{

    private File infrastructuralConfigFile;


    private File fileOutputRootdir;
    private File previewOutputRootdir;
    private File snapshotOutputRootdir;
    private String[] reklamefileRootDirectories;

    private File lockDir;
    private int fileDepth;


    private String fileFinderUrl;
    private int maxFilesFetched;


    private String domsEndpoint;
    private String domsUsername;
    private String domsPassword;

    private CentralWebservice domsApi = null;


    public InfrastructureContext() {
    }

    public File getInfrastructuralConfigFile() {
        return infrastructuralConfigFile;
    }

    public void setInfrastructuralConfigFile(File infrastructuralConfigFile) {
        this.infrastructuralConfigFile = infrastructuralConfigFile;
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


    public CentralWebservice getDomsApi() {
        return domsApi;
    }

    public void setDomsApi(CentralWebservice domsApi) {
        this.domsApi = domsApi;
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

    public String[] getReklamefileRootDirectories() {
        return reklamefileRootDirectories;
    }

    public void setReklamefileRootDirectories(String[] reklamefileRootDirectories) {
        this.reklamefileRootDirectories = reklamefileRootDirectories;
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
}