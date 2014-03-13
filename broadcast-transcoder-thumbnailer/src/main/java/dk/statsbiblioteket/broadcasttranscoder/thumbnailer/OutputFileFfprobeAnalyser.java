package dk.statsbiblioteket.broadcasttranscoder.thumbnailer;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorChainElement;
import dk.statsbiblioteket.broadcasttranscoder.processors.ProcessorException;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.ExternalJobRunner;
import dk.statsbiblioteket.broadcasttranscoder.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by csr on 13/03/14.
 */
public class OutputFileFfprobeAnalyser extends ProcessorChainElement {

    private static Logger logger = LoggerFactory.getLogger(OutputFileFfprobeAnalyser.class);

    @Override
    protected <T extends TranscodingRecord> void processThis(TranscodeRequest request, SingleTranscodingContext<T> context) throws ProcessorException {
        File outputFile = FileUtils.findFinalMediaOutputFile(request, context);
        String command = "ffprobe " + outputFile.getAbsolutePath() + " -show_format ";
        logger.info("Executing '" + command + "'");
        ExternalJobRunner runner;
        try {
            runner = new ExternalJobRunner(new String[]{"bash", "-c", command});
            logger.debug("Command '" + command + "' returned with output '" + runner.getOutput());
            logger.debug("Command '" + command + "' returned with stderror '" + runner.getError());
        } catch (Exception e) {
            throw new ProcessorException("Failed to run command "+command,e);
        }
        String[] commandOutput = runner.getOutput().split("\\n");
        String[] commandError = runner.getError().split("\\n");
        findDAR(commandError, request);
        findLengthSeconds(commandOutput, request);
    }

    private void findDAR (String[] lines, TranscodeRequest request) {
        Pattern videoPattern = Pattern.compile(".*Stream.*DAR\\s(([0-9]*):([0-9]*)).*");
        for (String line:lines) {
            Matcher darMatcher = videoPattern.matcher(line);
            if (darMatcher.matches()) {
                String top = darMatcher.group(3);
                String bottom = darMatcher.group(4);
                logger.debug("Matched DAR '" + top + ":" + bottom);
                final double displayAspectRatio = Double.parseDouble(top) / Double.parseDouble(bottom);
                logger.info("Detected aspect ratio '" + displayAspectRatio + "' for '" + request.getObjectPid() + "'");
                request.setDisplayAspectRatio(displayAspectRatio);
                request.setDisplayAspectRatioString(top + ":" + bottom);
            }
        }
    }

    private void findLengthSeconds (String[] lines, TranscodeRequest request) {
        Pattern durationPattern = Pattern.compile("duration=(([0-9]*).([0-9]*))");
        for (String line:lines) {
            Matcher durationMatcher = durationPattern.matcher(line);
            if (durationMatcher.matches()) {
                Float duration = Float.parseFloat(durationMatcher.group(1));
                request.setFfprobeDurationSeconds(duration);
                logger.info("Found duration {}s for {}.", duration, request.getObjectPid());
            }
        }
    }

}
