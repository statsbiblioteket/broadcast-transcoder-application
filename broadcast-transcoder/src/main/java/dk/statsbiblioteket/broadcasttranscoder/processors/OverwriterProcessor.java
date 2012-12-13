package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 12/13/12
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverwriterProcessor extends ProcessorChainElement {


    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {

        //check if overwrite false
        //check if file exist

        File file = FileUtils.findMediaOutputFile(request, context);
        if ( ! context.isOverwrite() && file != null && file.exists()){
        //if file exists and overwrite false, setGoForTranscoding = false
            request.setGoForTranscoding(false);
        }


    }
}
