package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;

/**
 *
 */
public class TranscoderDispatcherProcessor extends ProcessorChainElement {

    public TranscoderDispatcherProcessor() {
    }

    public TranscoderDispatcherProcessor(ProcessorChainElement childElement) {
        super(childElement);
    }

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
                //final ProgramStreamTranscoderProcessor childElement = new ProgramStreamTranscoderProcessor();
                //this.setChildElement(childElement);
                //childElement.setChildElement(new TranscoderPersistenceProcessor());
                this.setChildElement(new PidExtractorProcessor());
                break;
            case AUDIO_WAV:
                final WavTranscoderProcessor childElement1 = new WavTranscoderProcessor();
                this.setChildElement(childElement1);
                childElement1.setChildElement(new TranscoderPersistenceProcessor());
                break;
        }
    }
}
