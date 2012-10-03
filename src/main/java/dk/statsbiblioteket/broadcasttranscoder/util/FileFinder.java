package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 10/3/12
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FileFinder {
    Map<BroadcastMetadata, File> findAndBringOnline(TranscodeRequest request, Context context) throws ProcessorException;
}
