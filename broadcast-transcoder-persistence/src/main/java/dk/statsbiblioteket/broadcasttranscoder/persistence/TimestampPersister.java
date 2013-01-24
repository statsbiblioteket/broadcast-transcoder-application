package dk.statsbiblioteket.broadcasttranscoder.persistence;

/**
 * Encapsulates the functionality for persisting doms timestamps for transcoded files.
 */
public interface TimestampPersister {

    /**
     * Get the DOMS timestamp for the most recent transcoding of a given program.
     * @param programpid The doms uuid of the program.
     * @return The unix timestamp of the last transcoding or null if none is known in the database.
     */
    Long getTimestamp(String programpid);

    /**
     * Sets the DOMS timestamp for the most recent transcoding of a given program.
     * @param programpid the doms uuid of the program.
     * @param timestamp The unix timestamp of the new transcoding.
     */
    void setTimestamp(String programpid, long timestamp);

}
