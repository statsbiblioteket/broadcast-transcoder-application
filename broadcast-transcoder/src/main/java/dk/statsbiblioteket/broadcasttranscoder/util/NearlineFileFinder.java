package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
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
public class NearlineFileFinder implements FileFinder {

    private static final Logger logger = LoggerFactory.getLogger(NearlineFileFinder.class);


    /**
     * Given a list of files to bring online, bring them online and return a map listing their local locations.
     * @return
     */
    @Override
    public Map<BroadcastMetadata, File> findAndBringOnline(TranscodeRequest request, Context context) throws ProcessorException {
        String finderBaseUrl = context.getFileFinderUrl();
        List<BroadcastMetadata> metadatas = request.getBroadcastMetadata();
        int max_files = context.getMaxFilesFetched();

        Map<BroadcastMetadata, File> result = new HashMap<BroadcastMetadata, File>();
        if (metadatas.size() > max_files) {
            //TODO how about paging the stuff instead?
            throw new ProcessorException("Tried to fetch " + metadatas.size() + " at a time. Disallowed.");
        }
        String query = "";
        for (BroadcastMetadata metadata: metadatas) {
            if (query.length() > 0) {
                query+="&";
            }
            query+=metadata.getFilename();
        }
        String url = finderBaseUrl + "?" + query;
        logger.debug("Executing file finder query: " + url);
        InputStream is = null;
        try {
            is = new URL(url).openStream();
        } catch (IOException e) {
            throw new ProcessorException("Failed to open URL "+url,e);
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
            throw new ProcessorException("IOException while reading file",e);
        }
        try {
            is.close();
        } catch (IOException e) {
            throw new ProcessorException("IOException when closing file",e);
        }
        if (metadatas.size() != result.size()) {
            throw new ProcessorException("Found " + result.size() + " files. Expected " + metadatas.size() + ".");
        }
        return result;
    }

    /**
     * Given a list of files to bring online, bring them online and return a map listing their local locations.
     * @return
     */
    @Override
    public boolean isAllFilesOnline(TranscodeRequest request, Context context) throws ProcessorException {
        String finderBaseUrl = context.getFileFinderUrl();
        List<BroadcastMetadata> metadatas = request.getBroadcastMetadata();

        boolean allIsOnline = true;

        for (BroadcastMetadata metadata : metadatas) {
            String filename = metadata.getFilename();
            String url = finderBaseUrl + "?" + "*"+filename;
            try {
                InputStream is = new URL(url).openStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                int lines = 0;
                while (bufferedReader.readLine() != null){
                    lines++;
                }
                is.close();
                if (lines == 0){
                    allIsOnline = false;
                    break;
                }
            } catch (IOException e) {
                throw new ProcessorException("Failed to open URL "+url,e);
            }

        }
        return allIsOnline;
    }
}
