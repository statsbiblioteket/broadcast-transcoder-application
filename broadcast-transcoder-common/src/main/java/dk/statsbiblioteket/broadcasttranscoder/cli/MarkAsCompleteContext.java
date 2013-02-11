package dk.statsbiblioteket.broadcasttranscoder.cli;

import dk.statsbiblioteket.broadcasttranscoder.cli.ProgramAnalyzerContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/11/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarkAsCompleteContext<T extends TranscodingRecord> extends InfrastructureContext<T> {

    List<Pair<String,Long>> records = new ArrayList<Pair<String, Long>>();



    public List<Pair<String, Long>> getRecords() {
        return records;
    }

    public void setRecords(List<Pair<String, Long>> records) {
        this.records = records;
    }
}
