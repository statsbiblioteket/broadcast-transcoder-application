package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.ProgramAnalyzerContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.util.MockFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This processor brings each file in the BroadcastMetadata online, and initialises the Filemap
 */
public class FilefinderFetcherFakerProcessor extends ProcessorChainElement {


    protected void processThis(TranscodeRequest request, SingleTranscodingContext context) throws ProcessorException {

        HashMap<BroadcastMetadata, File> fileMap = new HashMap<BroadcastMetadata, File>();
        if (context instanceof ProgramAnalyzerContext) {
            ProgramAnalyzerContext programAnalyzerContext = (ProgramAnalyzerContext) context;
            for (BroadcastMetadata broadcastMetadata : request.getBroadcastMetadata()) {
                Long length;
                Map<String,Long> fileLengthList = programAnalyzerContext.getFileLengthList();
                length = fileLengthList.get(broadcastMetadata.getFilename());
                if (length != null){
                    MockFile file = new MockFile(broadcastMetadata.getFilename(), length);
                    fileMap.put(broadcastMetadata,file);
                }
            }
        }
        request.setFileMap(fileMap);

    }
}
