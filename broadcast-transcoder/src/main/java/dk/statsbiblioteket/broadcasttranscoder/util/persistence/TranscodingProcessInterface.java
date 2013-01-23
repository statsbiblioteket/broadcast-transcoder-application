package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 1/23/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TranscodingProcessInterface<T> extends GenericDAO<T, String>, TimestampPersister {
    List<BroadcastTranscodingRecord> getAllTranscodings(long since, TranscodingState state);

    void markAsChangedInDoms(String programpid, long timestamp);

    void markAsAlreadyTranscoded(String programpid);

    void markAsFailed(String programpid, String message);

    void markAsRejected(String programpid, String message);
}
