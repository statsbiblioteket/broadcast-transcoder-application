package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This processor resolves the input pid to a locally mounted mediafile and store it in the
 * clipperCommand field of the request (since we always just clip the whole file).
 */
public class ReklamefilmFileResolverProcessor extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(ReklamefilmFileResolverProcessor.class);


    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {
        ReklamefilmFileResolver resolver = context.getReklamefilmFileResolver();
        String pid = request.getObjectPid();
        File mediafile = resolver.resolverPidToLocalFile(pid);
        request.setClipperCommand("'" + mediafile.getAbsolutePath() + "'");
        logger.info("Resolved " + pid + " to " + request.getClipperCommand());
    
    
    
        decorateReklamefile(request, context, mediafile);
    
    }
    
    public static <T extends TranscodingRecord> void decorateReklamefile(TranscodeRequest request,
                                                                   SingleTranscodingContext<T> context,
                                                                         File mediafile)
            throws ProcessorException {
    
        final long nominalDurationSeconds = 300L;
        
        //Bitrate is not used for Reklamefilm, so why is it set?
        request.setBitrate(mediafile.length()/nominalDurationSeconds);
        
        //If timeout is not set, it will be calculated from stuff that might not be set for reklamefilms, so just set it
        request.setTimeoutMilliseconds((long) (nominalDurationSeconds*1000L/context.getTranscodingTimeoutDivisor()));
        
        //Assume that all source files are Mpegs
        request.setFileFormat(FileFormatEnum.MPEG_PS);
        
        //There is just one source file (clip)
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip(mediafile.getAbsolutePath());
        //This is an exact file, so start at 0
        clip.setFileStartTime(0L);
        //Rather that trying to guess the length, just set the length to something. I do not thing any of them are longer than this
        
        clip.setFileEndTime(clip.getFileStartTime()+nominalDurationSeconds*1000);
        List<TranscodeRequest.FileClip> clips = new ArrayList<TranscodeRequest.FileClip>();
        clips.add(clip);
        request.setClips(clips);
        
        //This file is exact, so do not cut
        request.setHasExactFile(true);

    }
}
