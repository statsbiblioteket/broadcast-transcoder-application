package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import java.io.File;

/**
 *
 */
public interface ReklamefilmFileResolver {

    //TODO create a working implementation
    /**
     * Resolve the pid of a doms reklamefilm object to a locally
     * mounted file, or null if it cannot be resolved.
     * @param domsReklamePid  the pid
     * @return the File
     */
    File resolverPidToLocalFile(String domsReklamePid);

}
