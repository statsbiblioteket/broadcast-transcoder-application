package dk.statsbiblioteket.broadcasttranscoder.processors;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.util.FileFormatEnum;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

public class PidAndAsepctRatioExtractorProcessorTest {
    
    @Test
    public void parseFFProbeOutput_mux1() throws ProcessorException {
    
        String ffprobeOutput = "ffprobe version 4.0 Copyright (c) 2007-2018 the FFmpeg developers\n"
                               + "  built with gcc 7 (GCC)\n"
                               + "  configuration: --prefix=/opt/ffmpeg40 --libdir=/opt/ffmpeg40/lib64 --extra-cflags='-I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include -I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include/opus' --extra-ldflags=-L/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/lib64 --disable-stripping --pkg-config-flags=--static --enable-gpl --enable-nonfree --enable-libmp3lame --enable-libopus --enable-libvorbis --enable-libx264 --enable-libfdk-aac --x86asmexe=nasm213 --ranlib=/bin/true --enable-libx265 --enable-libzvbi\n"
                               + "  libavutil      56. 14.100 / 56. 14.100\n"
                               + "  libavcodec     58. 18.100 / 58. 18.100\n"
                               + "  libavformat    58. 12.100 / 58. 12.100\n"
                               + "  libavdevice    58.  3.100 / 58.  3.100\n"
                               + "  libavfilter     7. 16.100 /  7. 16.100\n"
                               + "  libswscale      5.  1.100 /  5.  1.100\n"
                               + "  libswresample   3.  1.100 /  3.  1.100\n"
                               + "  libpostproc    55.  1.100 / 55.  1.100\n"
                               + "Input #0, mpegts, from 'mux1.1321282800-2011-11-14-16.00.00_1321286400-2011-11-14-17.00.00_dvb1-1.ts':\n"
                               + "  Duration: 01:00:00.36, start: 10795.517367, bitrate: 19903 kb/s\n"
                               + "  Program 81 \n"
                               + "    Metadata:\n"
                               + "      service_name    : OAD MUX1\n"
                               + "      service_provider: DIGI-TV\n"
                               + "  Program 101 \n"
                               + "    Metadata:\n"
                               + "      service_name    : DR1\n"
                               + "      service_provider: DR\n"
                               + "    Stream #0:0[0x6f]: Video: mpeg2video (Main) ([2][0][0][0] / 0x0002), yuv420p(tv, bt470bg, top first), 704x576 [SAR 16:11 DAR 16:9], 25 fps, 25 tbr, 90k tbn, 50 tbc\n"
                               + "    Stream #0:1[0x79](dan): Audio: mp2 ([3][0][0][0] / 0x0003), 48000 Hz, stereo, fltp, 256 kb/s\n"
                               + "    Stream #0:2[0x83](dan): Subtitle: dvb_teletext ([6][0][0][0] / 0x0006)\n"
                               + "  Program 102 \n"
                               + "    Metadata:\n"
                               + "      service_name    : DR2\n"
                               + "      service_provider: DR\n"
                               + "    Stream #0:10[0xd3]: Video: mpeg2video (Main) ([2][0][0][0] / 0x0002), yuv420p(tv, bt470bg, top first), 704x576 [SAR 16:11 DAR 16:9], 25 fps, 25 tbr, 90k tbn, 50 tbc\n"
                               + "    Stream #0:11[0xdd](dan): Audio: mp2 ([3][0][0][0] / 0x0003), 48000 Hz, stereo, s16p, 256 kb/s\n"
                               + "    Stream #0:3[0xe7](dan): Subtitle: dvb_teletext\n"
                               + "  Program 111 \n"
                               + "    Metadata:\n"
                               + "      service_name    : DR Synstolkning\n"
                               + "      service_provider: DR\n"
                               + "    Stream #0:0[0x6f]: Video: mpeg2video (Main) ([2][0][0][0] / 0x0002), yuv420p(tv, bt470bg, top first), 704x576 [SAR 16:11 DAR 16:9], 25 fps, 25 tbr, 90k tbn, 50 tbc\n"
                               + "    Stream #0:12[0x7a](dan): Audio: mp2 ([3][0][0][0] / 0x0003), 48000 Hz, stereo, s16p, 256 kb/s\n"
                               + "    Stream #0:2[0x83](dan): Subtitle: dvb_teletext ([6][0][0][0] / 0x0006)\n"
                               + "  Program 213 \n"
                               + "    Metadata:\n"
                               + "      service_name    : ?TV 2 (�stjylland)\n"
                               + "      service_provider: TV 2\n"
                               + "    Stream #0:6[0x83f]: Video: mpeg2video (Main) ([2][0][0][0] / 0x0002), yuv420p(tv, bt470bg, top first), 704x576 [SAR 16:11 DAR 16:9], 25 fps, 25 tbr, 90k tbn, 50 tbc\n"
                               + "    Stream #0:5[0x849](dan): Audio: mp2 ([3][0][0][0] / 0x0003), 48000 Hz, stereo, s16p, 256 kb/s\n"
                               + "    Stream #0:9[0x853](dan,dan): Subtitle: dvb_teletext ([6][0][0][0] / 0x0006), 492x250\n"
                               + "    Stream #0:13[0x857](dan): Subtitle: dvb_subtitle ([6][0][0][0] / 0x0006)\n"
                               + "    Stream #0:14[0x858](dan): Subtitle: dvb_subtitle ([6][0][0][0] / 0x0006) (hearing impaired)\n"
                               + "  Program 413 \n"
                               + "    Metadata:\n"
                               + "      service_name    : ?�stjylland\n"
                               + "      service_provider: DIGI-TV\n"
                               + "    Stream #0:4[0x19b]: Video: mpeg2video (Main) ([2][0][0][0] / 0x0002), yuv420p(tv, bt470bg, top first), 704x576 [SAR 16:11 DAR 16:9], 25 fps, 25 tbr, 90k tbn, 50 tbc\n"
                               + "    Stream #0:7[0x1a5](dan): Audio: mp2 ([3][0][0][0] / 0x0003), 48000 Hz, stereo, s16p, 256 kb/s\n"
                               + "    Stream #0:8[0x1af](dan): Subtitle: dvb_teletext\n";
        TranscodeRequest request = new TranscodeRequest();
        request.setFileFormat(FileFormatEnum.MULTI_PROGRAM_MUX);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip("path");
        clip.setProgramId(213);
        request.setClips(Arrays.asList(clip));
        SingleTranscodingContext<TranscodingRecord> context = new SingleTranscodingContext<>();
        PidAndAsepctRatioExtractorProcessor.parseFFProbeOutput(request,context,ffprobeOutput);
        assertEquals("0x857",request.getDvbsubPid());
        assertEquals("0x83f",request.getVideoPid());
        assertEquals("0x849", request.getAudioStereoPid());
        assertEquals("0x849", request.getAudioPids().iterator().next());
        assertEquals(1, request.getAudioPids().size());
        assertEquals("mpgv",request.getVideoFcc());
        assertEquals("mpga",request.getAudioFcc());
        assertEquals("16:9", request.getDisplayAspectRatioString());
        assertEquals(16.0/9.0, request.getDisplayAspectRatio(), 0.001);
        System.out.println(request);
    }
    
    
    @Test
    public void parseFFProbeOutput_digivid() throws ProcessorException {
        String ffprobeOutput = "ffprobe version 4.0 Copyright (c) 2007-2018 the FFmpeg developers\n"
                               + "  built with gcc 7 (GCC)\n"
                               + "  configuration: --prefix=/opt/ffmpeg40 --libdir=/opt/ffmpeg40/lib64 --extra-cflags='-I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include -I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include/opus' --extra-ldflags=-L/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/lib64 --disable-stripping --pkg-config-flags=--static --enable-gpl --enable-nonfree --enable-libmp3lame --enable-libopus --enable-libvorbis --enable-libx264 --enable-libfdk-aac --x86asmexe=nasm213 --ranlib=/bin/true --enable-libx265 --enable-libzvbi\n"
                               + "  libavutil      56. 14.100 / 56. 14.100\n"
                               + "  libavcodec     58. 18.100 / 58. 18.100\n"
                               + "  libavformat    58. 12.100 / 58. 12.100\n"
                               + "  libavdevice    58.  3.100 / 58.  3.100\n"
                               + "  libavfilter     7. 16.100 /  7. 16.100\n"
                               + "  libswscale      5.  1.100 /  5.  1.100\n"
                               + "  libswresample   3.  1.100 /  3.  1.100\n"
                               + "  libpostproc    55.  1.100 / 55.  1.100\n"
                               + "Input #0, mpegts, from 'dr1_digivid_970083300-2000-09-27-21.35.00_970097700-2000-09-28-01.35.00.ts':\n"
                               + "  Duration: 04:00:06.75, start: 1.493333, bitrate: 20976 kb/s\n"
                               + "  Program 1 \n"
                               + "    Stream #0:0[0x1011]: Video: h264 (Main) ([27][0][0][0] / 0x001B), yuv420p(top first), 720x576 [SAR 12:11 DAR 15:11], 25 fps, 25 tbr, 90k tbn, 50 tbc\n"
                               + "    Stream #0:1[0x1100]: Audio: mp2 ([3][0][0][0] / 0x0003), 48000 Hz, stereo, fltp, 384 kb/s";
    
        TranscodeRequest request = new TranscodeRequest();
        request.setFileFormat(FileFormatEnum.SINGLE_PROGRAM_VIDEO_TS);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip("path");
        clip.setProgramId(1);
        request.setClips(Arrays.asList(clip));
        SingleTranscodingContext<TranscodingRecord> context = new SingleTranscodingContext<>();
        PidAndAsepctRatioExtractorProcessor.parseFFProbeOutput(request,context,ffprobeOutput);
        assertEquals(null,request.getDvbsubPid());
        assertEquals("0x1011",request.getVideoPid());
        assertEquals("0x1100", request.getAudioStereoPid());
        assertEquals("0x1100", request.getAudioPids().iterator().next());
        assertEquals(1, request.getAudioPids().size());
        assertEquals("h264",request.getVideoFcc());
        assertEquals("mpga",request.getAudioFcc());
        assertEquals("15:11", request.getDisplayAspectRatioString());
        assertEquals(15.0/11.0, request.getDisplayAspectRatio(), 0.001);
        System.out.println(request);
    
    }
    
    
    @Test
    public void parseFFProbeOutput_yousee() throws ProcessorException {
        String ffprobeOutput = "ffprobe version 4.0 Copyright (c) 2007-2018 the FFmpeg developers\n"
                               + "  built with gcc 7 (GCC)\n"
                               + "  configuration: --prefix=/opt/ffmpeg40 --libdir=/opt/ffmpeg40/lib64 --extra-cflags='-I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include -I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include/opus' --extra-ldflags=-L/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/lib64 --disable-stripping --pkg-config-flags=--static --enable-gpl --enable-nonfree --enable-libmp3lame --enable-libopus --enable-libvorbis --enable-libx264 --enable-libfdk-aac --x86asmexe=nasm213 --ranlib=/bin/true --enable-libx265 --enable-libzvbi\n"
                               + "  libavutil      56. 14.100 / 56. 14.100\n"
                               + "  libavcodec     58. 18.100 / 58. 18.100\n"
                               + "  libavformat    58. 12.100 / 58. 12.100\n"
                               + "  libavdevice    58.  3.100 / 58.  3.100\n"
                               + "  libavfilter     7. 16.100 /  7. 16.100\n"
                               + "  libswscale      5.  1.100 /  5.  1.100\n"
                               + "  libswresample   3.  1.100 /  3.  1.100\n"
                               + "  libpostproc    55.  1.100 / 55.  1.100\n"
                               + "Input #0, mpegts, from 'tv2d_yousee.1447524000-2015-11-14-19.00.00_1447527600-2015-11-14-20.00.00_yousee.ts':\n"
                               + "  Duration: 01:00:00.76, start: 67578.146533, bitrate: 8929 kb/s\n"
                               + "  Program 2 \n"
                               + "    Stream #0:0[0x21]: Video: h264 (High) ([27][0][0][0] / 0x001B), yuv420p(tv, bt709, progressive), 1280x720 [SAR 1:1 DAR 16:9], 50 fps, 50 tbr, 90k tbn, 100 tbc\n"
                               + "    Stream #0:1[0x24]: Audio: mp2 ([3][0][0][0] / 0x0003), 48000 Hz, stereo, fltp, 192 kb/s\n"
                               + "    Stream #0:2[0x26](dan): Subtitle: dvb_teletext ([6][0][0][0] / 0x0006)\n"
                               + "    Stream #0:3[0x25]: Audio: ac3 ([6][0][0][0] / 0x0006), 48000 Hz, 5.1(side), fltp, 448 kb/s\n";
        
        TranscodeRequest request = new TranscodeRequest();
        request.setFileFormat(FileFormatEnum.SINGLE_PROGRAM_VIDEO_TS);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip("path");
        clip.setProgramId(2);
        request.setClips(Arrays.asList(clip));
        SingleTranscodingContext<TranscodingRecord> context = new SingleTranscodingContext<>();
        PidAndAsepctRatioExtractorProcessor.parseFFProbeOutput(request,context,ffprobeOutput);
        assertEquals(null,request.getDvbsubPid());
        assertEquals("0x21",request.getVideoPid());
        assertEquals("0x24", request.getAudioStereoPid());
        Iterator<String> audioPids = request.getAudioPids().iterator();
        assertEquals("0x25", audioPids.next());
        assertEquals("0x24", audioPids.next());
        assertEquals(2, request.getAudioPids().size());
        assertEquals("h264",request.getVideoFcc());
        assertEquals("a52",request.getAudioFcc());
        assertEquals("16:9", request.getDisplayAspectRatioString());
        assertEquals(16.0/9.0, request.getDisplayAspectRatio(), 0.001);
        System.out.println(request);
        
    }
    
    @Test
    public void parseFFProbeOutput_teracom() throws ProcessorException {
        String ffprobeOutput = "ffprobe version 4.0 Copyright (c) 2007-2018 the FFmpeg developers\n"
                               + "  built with gcc 7 (GCC)\n"
                               + "  configuration: --prefix=/opt/ffmpeg40 --libdir=/opt/ffmpeg40/lib64 --extra-cflags='-I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include -I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include/opus' --extra-ldflags=-L/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/lib64 --disable-stripping --pkg-config-flags=--static --enable-gpl --enable-nonfree --enable-libmp3lame --enable-libopus --enable-libvorbis --enable-libx264 --enable-libfdk-aac --x86asmexe=nasm213 --ranlib=/bin/true --enable-libx265 --enable-libzvbi\n"
                               + "  libavutil      56. 14.100 / 56. 14.100\n"
                               + "  libavcodec     58. 18.100 / 58. 18.100\n"
                               + "  libavformat    58. 12.100 / 58. 12.100\n"
                               + "  libavdevice    58.  3.100 / 58.  3.100\n"
                               + "  libavfilter     7. 16.100 /  7. 16.100\n"
                               + "  libswscale      5.  1.100 /  5.  1.100\n"
                               + "  libswresample   3.  1.100 /  3.  1.100\n"
                               + "  libpostproc    55.  1.100 / 55.  1.100\n"
                               + "Input #0, mpegts, from 'dr1_teracom.1529928000-2018-06-25-14.00.00_1529931600-2018-06-25-15.00.00_teracom.ts':\n"
                               + "  Duration: 01:00:00.95, start: 9688.794411, bitrate: 5988 kb/s\n"
                               + "  Program 101 \n"
                               + "    Metadata:\n"
                               + "      service_name    : ?DR1_b\n"
                               + "      service_provider: ?DR\n"
                               + "    Stream #0:0[0x6f]: Video: h264 (High) ([27][0][0][0] / 0x001B), yuv420p(tv, bt709, progressive), 1280x720 [SAR 1:1 DAR 16:9], 50 fps, 50 tbr, 90k tbn, 100 tbc\n"
                               + "    Stream #0:1[0x79](dan): Audio: aac_latm (HE-AAC) ([17][0][0][0] / 0x0011), 48000 Hz, stereo, fltp\n"
                               + "    Stream #0:2[0x83](dan): Subtitle: dvb_teletext ([6][0][0][0] / 0x0006)\n"
                               + "    Stream #0:3[0x87](dan): Subtitle: dvb_subtitle ([6][0][0][0] / 0x0006)\n"
                               + "    Stream #0:4[0x88](dan): Subtitle: dvb_subtitle ([6][0][0][0] / 0x0006) (hearing impaired)\n";
        
        TranscodeRequest request = new TranscodeRequest();
        request.setFileFormat(FileFormatEnum.SINGLE_PROGRAM_VIDEO_TS);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip("path");
        clip.setProgramId(101);
        request.setClips(Arrays.asList(clip));
        SingleTranscodingContext<TranscodingRecord> context = new SingleTranscodingContext<>();
        PidAndAsepctRatioExtractorProcessor.parseFFProbeOutput(request,context,ffprobeOutput);
        assertEquals("0x87",request.getDvbsubPid());
        assertEquals("0x6f",request.getVideoPid());
        assertEquals("0x79", request.getAudioStereoPid());
        Iterator<String> audioPids = request.getAudioPids().iterator();
        assertEquals("0x79", audioPids.next());
        assertEquals(1, request.getAudioPids().size());
        assertEquals("h264",request.getVideoFcc());
        assertEquals("mp4a",request.getAudioFcc());
        assertEquals("16:9", request.getDisplayAspectRatioString());
        assertEquals(16.0/9.0, request.getDisplayAspectRatio(), 0.001);
        System.out.println(request);
        
    }
    
    
    @Test
    public void parseFFProbeOutput_mpeg() throws ProcessorException {
        String ffprobeOutput = "ffprobe version 4.0 Copyright (c) 2007-2018 the FFmpeg developers\n"
                               + "  built with gcc 7 (GCC)\n"
                               + "  configuration: --prefix=/opt/ffmpeg40 --libdir=/opt/ffmpeg40/lib64 --extra-cflags='-I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include -I/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/include/opus' --extra-ldflags=-L/builddir/build/BUILD/ffmpeg_sources/ffmpeg-4.0/../ffmpeg_build/opt/ffmpeg40/lib64 --disable-stripping --pkg-config-flags=--static --enable-gpl --enable-nonfree --enable-libmp3lame --enable-libopus --enable-libvorbis --enable-libx264 --enable-libfdk-aac --x86asmexe=nasm213 --ranlib=/bin/true --enable-libx265 --enable-libzvbi\n"
                               + "  libavutil      56. 14.100 / 56. 14.100\n"
                               + "  libavcodec     58. 18.100 / 58. 18.100\n"
                               + "  libavformat    58. 12.100 / 58. 12.100\n"
                               + "  libavdevice    58.  3.100 / 58.  3.100\n"
                               + "  libavfilter     7. 16.100 /  7. 16.100\n"
                               + "  libswscale      5.  1.100 /  5.  1.100\n"
                               + "  libswresample   3.  1.100 /  3.  1.100\n"
                               + "  libpostproc    55.  1.100 / 55.  1.100\n"
                               + "Input #0, mpeg, from 'tv2-kabel_210.250_K10-TV2-Kabel_mpeg2_20091112045601_20091113045501_encoder7-2.mpeg':\n"
                               + "  Duration: 23:58:51.76, start: 0.276122, bitrate: 6979 kb/s\n"
                               + "    Stream #0:0[0x1e0]: Video: mpeg2video (Main), yuv420p(tv, top first), 720x576 [SAR 16:15 DAR 4:3], 6500 kb/s, 25 fps, 25 tbr, 90k tbn, 50 tbc\n"
                               + "    Stream #0:1[0x1c0]: Audio: mp2, 48000 Hz, stereo, s16p, 192 kb/s\n";
        
        TranscodeRequest request = new TranscodeRequest();
        request.setFileFormat(FileFormatEnum.MPEG_PS);
        TranscodeRequest.FileClip clip = new TranscodeRequest.FileClip("path");
        clip.setProgramId(101);
        SingleTranscodingContext<TranscodingRecord> context = new SingleTranscodingContext<>();
        PidAndAsepctRatioExtractorProcessor.parseFFProbeOutput(request,context,ffprobeOutput);
        assertEquals(null,request.getDvbsubPid());
        assertEquals("0x1e0",request.getVideoPid());
        assertEquals("0x1c0", request.getAudioStereoPid());
        Iterator<String> audioPids = request.getAudioPids().iterator();
        assertEquals("0x1c0", audioPids.next());
        assertEquals(1, request.getAudioPids().size());
        assertEquals("mpgv",request.getVideoFcc());
        assertEquals("mpga",request.getAudioFcc());
        assertEquals("4:3", request.getDisplayAspectRatioString());
        assertEquals(4.0/3.0, request.getDisplayAspectRatio(), 0.001);
        System.out.println(request);
        
    }
    
    
}