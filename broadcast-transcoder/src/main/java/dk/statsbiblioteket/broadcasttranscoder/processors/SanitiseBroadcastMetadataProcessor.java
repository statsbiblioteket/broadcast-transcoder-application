package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 12/21/12
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SanitiseBroadcastMetadataProcessor extends ProcessorChainElement {



    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        List<BroadcastMetadata> bm = request.getBroadcastMetadata();
        Map<String,List<BroadcastMetadata>> formatsMap = new HashMap<String, List<BroadcastMetadata>>();
        for (BroadcastMetadata metadata : bm) {
            String format = getFormat(metadata);
            List<BroadcastMetadata> list = formatsMap.get(format);
            if (list == null){

                list = new ArrayList<BroadcastMetadata>();
                formatsMap.put(format,list);
            }
            list.add(metadata);
        }
        if (formatsMap.keySet().size() > 1 ){
            String format = pickBestFormat(formatsMap.keySet());
            request.setBroadcastMetadata(formatsMap.get(format));
        }
        ArrayList<String> toRemove = new ArrayList<String>();

        for (Map.Entry<String, BroadcastMetadata> entry : request.getPidMap().entrySet()) {
            if ( ! request.getBroadcastMetadata().contains(entry.getValue())){
                toRemove.add(entry.getKey());
            }
        }
        for (String s : toRemove) {
            request.getPidMap().remove(s);
        }

    }

    private String pickBestFormat(Set<String> formats) {
        String bestFormat = "";

        for (String format : formats) {
            if (score(format) > score(bestFormat)){
                bestFormat = format;
            }
        }
        return bestFormat;
    }

    private int score(String format) {
        return format.length();
    }

    private String getFormat(BroadcastMetadata metadata) {
        return metadata.getFormat();
    }

}
