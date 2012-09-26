package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/26/12
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class NearlineFileFinder {

    private static final Logger logger = LoggerFactory.getLogger(NearlineFileFinder.class);
    private static final String NEARLINE_SERVICE = "http://plufire/~bart/stage_files.cgi";
    private static final int MAX_FILES = 10;


    /**
     * Given a list of files to bring online, bring them online and return a map listing their local locations.
     * @param metadatas
     * @return
     */
    public static Map<BroadcastMetadata, File> findAndBringOnline(List<BroadcastMetadata> metadatas) throws ProcessorException {
        Map<BroadcastMetadata, File> result = new HashMap<BroadcastMetadata, File>();
        if (metadatas.size() > MAX_FILES) {
            throw new ProcessorException("Tried to fetch " + metadatas.size() + " at a time. Disallowed.");
        }
        String query = "";
        for (BroadcastMetadata metadata: metadatas) {
            if (query.length() > 0) {
                query+="&";
            }
            query+=metadata.getFilename();
        }
        String url = NEARLINE_SERVICE + "?" + query;
        logger.debug("Executing file finder query: " + url);
        InputStream is = null;
        try {
            is = new URL(url).openStream();
        } catch (IOException e) {
            throw new ProcessorException(e);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line=bufferedReader.readLine())!=null) {
                logger.debug("Reading line: '" + line + "'");
                String filepath = line.trim();
                for (BroadcastMetadata metadata: metadatas) {
                    if (filepath.endsWith(metadata.getFilename())) {
                        result.put(metadata, new File(filepath));
                    }
                }
            }
        } catch (IOException e) {
            throw new ProcessorException(e);
        }
        try {
            is.close();
        } catch (IOException e) {
            throw new ProcessorException(e);
        }
        if (metadatas.size() != result.size()) {
            throw new ProcessorException("Found " + result.size() + " files. Expected " + metadatas.size() + ".");
        }
        return result;
    }

}
