package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.BroadcastMetadata;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This processor finds the source filetype with the best format, and removes the others from the list. This is to
 * handle the programs that overlap changes in broadcast type, such as going from analog to digital.
 */
public class SanitiseBroadcastMetadataProcessor extends ProcessorChainElement {



    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {        if (request.isHasExactFile()) {
            return;
        }
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
