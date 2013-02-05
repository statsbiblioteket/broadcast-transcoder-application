package dk.statsbiblioteket.broadcasttranscoder.cli.parsers;

import dk.statsbiblioteket.broadcasttranscoder.cli.GetJobsContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.InfrastructureContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.ProgramAnalyzerContext;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import org.apache.commons.cli.*;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/4/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgramAnalyzerOptionsParser<T extends TranscodingRecord> extends InfrastructureOptionsParser<T> {


    protected static final Option PROGRAMS = new Option("programList", true, "The list of programns to analyze");
    protected static final Option FILESIZES = new Option("fileSizes", true, "The list of filenames and sizes");
    private ProgramAnalyzerContext<T> context;


    public ProgramAnalyzerOptionsParser() {
        super();
        context = new ProgramAnalyzerContext<T>();

    }

    public ProgramAnalyzerContext<T> parseOptions(String[] args) throws OptionParseException {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException e) {
            parseError(e.toString());
            throw new OptionParseException(e.getMessage(), e);
        }
        parseUsageOption(cmd);
        parseInfrastructureConfigFileOption(cmd);
        parseHibernateConfigFileOption(cmd);
        parseProgramsListOption(cmd);
        parseFileLengthListOption(cmd);
        try {
            readInfrastructureProperties(context);

        } catch (IOException e) {
            throw new OptionParseException("Error reading properties.", e);
        }
        return context;
    }

    private void parseFileLengthListOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(FILESIZES.getOpt());
        if (configFileString == null) {
            parseError(FILESIZES.toString());
            throw new OptionParseException(FILESIZES.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line;

            while ((line = reader.readLine()) != null ) {
                String[] splits;
                splits = line.split(" ");
                String filename = splits[0];
                Long size = Long.parseLong(splits[1]);
                getContext().getFileLengthList().put(filename,size);
            }
        } catch (FileNotFoundException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        } catch (IOException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not readable.");
        }
    }

    private void parseProgramsListOption(CommandLine cmd) throws OptionParseException {
        String configFileString = cmd.getOptionValue(PROGRAMS.getOpt());
        if (configFileString == null) {
            parseError(PROGRAMS.toString());
            throw new OptionParseException(PROGRAMS.toString());
        }
        File configFile = new File(configFileString);
        if (!configFile.exists() || configFile.isDirectory()) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = reader.readLine()) != null ){
                String pid = line.trim();
                if ( ! pid.isEmpty()){
                    getContext().getPidList().add(pid);
                }
            }
        } catch (FileNotFoundException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        } catch (IOException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not readable.");
        }
    }


    @Override
    protected ProgramAnalyzerContext<T> getContext() {
        return context;
    }


}
