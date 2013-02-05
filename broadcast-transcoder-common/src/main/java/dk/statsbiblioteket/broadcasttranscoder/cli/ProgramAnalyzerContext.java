package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import org.apache.log4j.helpers.CyclicBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/4/13
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgramAnalyzerContext<T extends TranscodingRecord> extends SingleTranscodingContext<T>{
    private Map<String, Long> fileLengthList;
    private List<String> pidList;

    public Map<String, Long> getFileLengthList() {
        if (fileLengthList == null){
            fileLengthList = new HashMap<String, Long>();
        }
        return fileLengthList;
    }

    public List<String> getPidList() {
        if (pidList == null){
            pidList = new ArrayList<String>();
        }
        return pidList;
    }
}
