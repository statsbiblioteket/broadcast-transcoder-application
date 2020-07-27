package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 3/19/13
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class OnlineFileFinder implements FileFinder {
       private static final Logger logger = LoggerFactory.getLogger(OnlineFileFinder.class);



    @Override
    public Map<BroadcastMetadata, File> findAndBringOnline(TranscodeRequest request, InfrastructureContext context) throws ProcessorException {
        String finderBaseUrl = context.getOnlineFileFinderUrl();
        List<BroadcastMetadata> metadatas = request.getBroadcastMetadata();
        Map<BroadcastMetadata, File> result = new HashMap<BroadcastMetadata, File>();
        for (BroadcastMetadata metadata: metadatas) {
            String url = finderBaseUrl + metadata.getFilename();
            logger.debug("Executing file finder query: {}", url);
            InputStream is = null;
            try {
                is = new URL(url).openStream();
            } catch (IOException e) {
                throw new ProcessorException("Failed to open URL " + url,e);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                  logger.debug("File {} not found online.", metadata.getFilename());
                } else {
                  logger.debug("File {} found.", line);
                  result.put(metadata, new File(line.trim()));
                }
            } catch (IOException e) {
                throw new ProcessorException("Unexpected error finding files.", e);
            }
        }
        return result;
    }

    @Override
    public boolean isAllFilesOnline(TranscodeRequest request, InfrastructureContext context) throws ProcessorException {
        Map<BroadcastMetadata, File> fileMap = findAndBringOnline(request, context);
        return fileMap.size() == request.getBroadcastMetadata().size();

    }
}
