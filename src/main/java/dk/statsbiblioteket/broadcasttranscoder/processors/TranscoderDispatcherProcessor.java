package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;

/**
 *
 */
public class TranscoderDispatcherProcessor extends ProcessorChainElement {
    @Override
    protected void processThis(TranscodeRequest request, Context context) throws ProcessorException {
        switch (request.getFileFormat()) {
            case MULTI_PROGRAM_MUX:
                this.setChildElement(new PidExtractorProcessor());
                break;
            case SINGLE_PROGRAM_VIDEO_TS:
                this.setChildElement(new PidExtractorProcessor());
                break;
            case SINGLE_PROGRAM_AUDIO_TS:
                this.setChildElement(new PidExtractorProcessor());
                break;
            case MPEG_PS:
                throw new ProcessorException("mpeg transcoding not implemented");
                //break;
            case AUDIO_WAV:
                throw new ProcessorException("wav transcoding not implemented");
                //break;
        }
    }
}
