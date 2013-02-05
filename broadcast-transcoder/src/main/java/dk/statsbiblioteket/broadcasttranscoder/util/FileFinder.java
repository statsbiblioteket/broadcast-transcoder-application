package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;

import java.io.File;
import java.util.Map;

/**
 * Interface to the file storage system
 */
public interface FileFinder {

    /**
     * Attempt to bring all the files specified in the request online.
     * @param request the transcoding request
     * @param context The context we are running in
     * @return a map of BroadcastMetadata to the local files, that have been brought online
     * @throws ProcessorException
     */
    Map<BroadcastMetadata, File> findAndBringOnline(TranscodeRequest request, InfrastructureContext context) throws ProcessorException;


    /**
     * Verify that all the files referenced in the request are online
     * @param request the transcoding request
     * @param context The context we are running in
     * @return true if the files are now online.
     * @throws ProcessorException
     */
    boolean isAllFilesOnline(TranscodeRequest request, InfrastructureContext context) throws ProcessorException;
}
