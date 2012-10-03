package dk.statsbiblioteket.broadcasttranscoder;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.processors.BroadcastMetadataSorterProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.ClipFinderProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.CoverageAnalyserProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.FileMetadataFetcherProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.FilePropertiesIdentifierProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.FilefinderFetcherProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.PersistentMetadataExtractorProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.StructureFixerProcessor;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscoderDispatcherProcessor;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;
import junit.framework.TestCase;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 10/3/12
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClipTest extends TestCase {

    Context context = new Context();
    String mux2 =  "/home/csr/old_home/yousee_scratch/mux1.1323493200-2011-12-10-06.00.00_1323496800-2011-12-10-07.00.00_dvb1-1.ts";
    String wav = "/home/csr/old_home/yousee_scratch/rakl_106.200_Radio-Klassisk_pcm_20111203045601_20111204045502_encoder1-2.wav ";
    String mpeg = "/home/csr/old_home/yousee_scratch/tv3p_647.250_K43-TV3P_mpeg1_20111114045601_20111115045502_encoder6-2.mpeg";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context.setProgrampid("uuid:foobar");
        context.setAnalysisClipLength(100000000l);
        context.setAudioBitrate(96);
        context.setEndOffsetTS(10);
        context.setFileDepth(4);
        context.setGapToleranceSeconds(2);
        context.setLockDir(new File("/home/csr/old_home/yousee_scratch/lockdir"));
        context.setHibernateConfigFile(new File("/home/csr/projects/broadcast-transcoder-application/src/test/config/hibernate.in-memory_unittest.cfg.xml"));
        context.setMaxHole(10);
        context.setMaxMissingEnd(10);
        context.setMaxMissingStart(10);
        context.setPreviewLength(30);
        context.setPreviewOutputRootdir(new File("/home/csr/old_home/yousee_scratch/previewdir"));
        context.setPreviewTimeout(100);
        context.setSnapshotFrames(5);
        context.setSnapshotOutputRootdir(new File("/home/csr/old_home/yousee_scratch/snapshotdir"));
        context.setSnapshotPaddingSeconds(20);
        context.setSnapshotScale(26);
        context.setSnapshotTargetDenominator(9);
        context.setSnapshotTargetNumerator(16);
        context.setSnapshotTimeoutDivisor(1);
        context.setStartOffsetTS(-10);
        context.setTranscodingTimeoutDivisor(1);
        context.setVideoBitrate(400);
        context.setVideoHeight(288);
        context.setX264VlcParams("profile=High,preset=superfast,level=3.0");
        context.setX264FfmpegParams("-async 2 -vcodec libx264 -deinterlace -ar 44100 -preset superfast -threads 0");
        context.setSoxTranscodeParams(" -t raw -s -b 16 -c2");
        context.setFileOutputRootdir(new File("/home/csr/old_home/yousee_scratch/outputdir"));
    }

    public void testMultiProgramMux() throws ProcessorException, DatatypeConfigurationException {
        TranscodeRequest request = new TranscodeRequest();
        request.setFileFormat(FileFormatEnum.MULTI_PROGRAM_MUX);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip(mux2);
        clip.setClipLength(500000000L);
        clip.setProgramId(101);
        clip.setStartOffsetBytes(100000000L);
        List<TranscodeRequest.FileClip> clips = new ArrayList<TranscodeRequest.FileClip>();
        clips.add(clip);
        request.setClips(clips);


        ProgramBroadcast pb = new ProgramBroadcast();
        XMLGregorianCalendar xmlcalend = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(0,0,1,4,34));
        XMLGregorianCalendar xmlcalstart = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(0,0,1,4,30));
        pb.setTimeStart(xmlcalstart);
        pb.setTimeStop(xmlcalend);
        request.setProgramBroadcast(pb);

        ProcessorChainElement coverage = new CoverageAnalyserProcessor();
        ProcessorChainElement fixer = new StructureFixerProcessor();
        ProcessorChainElement dispatcher = new TranscoderDispatcherProcessor();
        coverage.setChildElement(fixer);
        fixer.setChildElement(dispatcher);
        dispatcher.processIteratively(request, context);
    }

    public void testWav() throws DatatypeConfigurationException, ProcessorException {
        TranscodeRequest request = new TranscodeRequest();
        request.setFileFormat(FileFormatEnum.AUDIO_WAV);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip(wav);
        clip.setClipLength(500000000L);
        clip.setStartOffsetBytes(100000000L);
        List<TranscodeRequest.FileClip> clips = new ArrayList<TranscodeRequest.FileClip>();
        clips.add(clip);
        request.setClips(clips);
        request.setBitrate(88125L);

        context.setProgrampid("uuid:barfoo");

        ProgramBroadcast pb = new ProgramBroadcast();
        XMLGregorianCalendar xmlcalend = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(0,0,1,5,0));
        XMLGregorianCalendar xmlcalstart = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(0,0,1,4,30));
        pb.setTimeStart(xmlcalstart);
        pb.setTimeStop(xmlcalend);
        request.setProgramBroadcast(pb);

        ProcessorChainElement dispatcher = new TranscoderDispatcherProcessor();
        dispatcher.processIteratively(request, context);

    }

    public void testMpeg() throws DatatypeConfigurationException, ProcessorException {
        TranscodeRequest request = new TranscodeRequest();
        request.setFileFormat(FileFormatEnum.MPEG_PS);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip(mpeg);
        clip.setClipLength(800000000L);
        clip.setStartOffsetBytes(200000000L);
        List<TranscodeRequest.FileClip> clips = new ArrayList<TranscodeRequest.FileClip>();
        clips.add(clip);
        request.setClips(clips);
        request.setBitrate(169250L);

        context.setProgrampid("uuid:carfoo");

        ProgramBroadcast pb = new ProgramBroadcast();
        XMLGregorianCalendar xmlcalend = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(0,0,1,5,0));
        XMLGregorianCalendar xmlcalstart = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(0,0,1,4,30));
        pb.setTimeStart(xmlcalstart);
        pb.setTimeStop(xmlcalend);
        request.setProgramBroadcast(pb);

        ProcessorChainElement dispatcher = new TranscoderDispatcherProcessor();
        dispatcher.processIteratively(request, context);

    }


}
