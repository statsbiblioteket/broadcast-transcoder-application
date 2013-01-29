package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingOptionsParser;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.processors.*;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 10/24/12
 * Time: 10:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class SingleFileClipper {

    public static void main(String[] args) throws OptionParseException, DatatypeConfigurationException, ProcessorException {
        SingleTranscodingContext context = new SingleTranscodingOptionsParser().parseOptions(args);
        String filename = args[0];
        String filelengthSeconds = args[1];
        TranscodeRequest request = new TranscodeRequest();
        long duration = Long.parseLong(filelengthSeconds);
        File file = new File(filename);
        request.setBitrate(file.length()/duration);
        FileFormatEnum format = null;
        if (request.getBitrate() > 100000l) {
            format = FileFormatEnum.SINGLE_PROGRAM_VIDEO_TS;
        } else {
            format = FileFormatEnum.SINGLE_PROGRAM_AUDIO_TS;
        }
        request.setFileFormat(format);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip(file.getAbsolutePath());
        List<TranscodeRequest.FileClip> clips = new ArrayList<TranscodeRequest.FileClip>();
        clips.add(clip);
        request.setClips(clips);
        context.setProgrampid("uuid:" + file.getName());
        ProgramBroadcast pb = new ProgramBroadcast();
        //XMLGregorianCalendar xmlcalend = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(0,0,1,4,35));
        long start = System.currentTimeMillis();
        long end = start + duration*1000L;


        final GregorianCalendar calStart = new GregorianCalendar();
        calStart.setTimeInMillis(start);
        final GregorianCalendar calEnd = new GregorianCalendar();
        calEnd.setTimeInMillis(end);
        XMLGregorianCalendar xmlcalstart = DatatypeFactory.newInstance().newXMLGregorianCalendar(calStart);

        XMLGregorianCalendar xmlcalend = DatatypeFactory.newInstance().newXMLGregorianCalendar(calEnd);

        pb.setTimeStart(xmlcalstart);
        pb.setTimeStop(xmlcalend);
        request.setProgramBroadcast(pb);
        ProcessorChainElement pider = new PidAndAsepctRatioExtractorProcessor();
        ProcessorChainElement concatenator = new ClipConcatenatorProcessor();
        ProcessorChainElement video = new UnistreamVideoTranscoderProcessor();
        ProcessorChainElement audio = new UnistreamVideoTranscoderProcessor();
        ProcessorChainElement chain = null;
        if (format.equals(FileFormatEnum.SINGLE_PROGRAM_AUDIO_TS)) {
            chain = ProcessorChainElement.makeChain(pider, concatenator, audio);
        } else {
            chain = ProcessorChainElement.makeChain(pider, concatenator, video);
        }
        chain.processIteratively(request, context);
    }

}
