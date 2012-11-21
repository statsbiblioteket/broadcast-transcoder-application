/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.fetcher.cli;

public class PropertyNames {

    private PropertyNames(){}

    /*
    Properties related to DOMS
     */
    public static final String DOMS_ENDPOINT = "domsWSAPIEndpointUrl";
    public static final String DOMS_USER = "domsUsername";
    public static final String DOMS_PASSWORD = "domsPassword";

    /*
    Properties related to file paths
     */
    public static final String FILE_DIR = "fileOutputDirectory";
    public static final String PREVIEW_DIR = "previewOutputDirectory";
    public static final String SNAPSHOT_DIR = "snapshotOutputDirectory";
    public static final String LOCK_DIR = "lockDirectory";
    public static final String FILE_DEPTH = "fileDepth";

    public static final String VIEW_ANGLE = "viewAngle";
    public static final String COLLECTION = "collection";
    public static final String STATE = "state";
    public static final String BATCH_SIZE = "batchSize";

    /*
    Properties related to nearline storage
     */
    public static final String FILE_FINDER = "nearlineFilefinderUrl";
    public static final String MAX_FILES_FETCHED = "maxFilesFetched";

}
