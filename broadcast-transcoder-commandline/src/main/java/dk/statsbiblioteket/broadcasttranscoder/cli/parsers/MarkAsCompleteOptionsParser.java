package dk.statsbiblioteket.broadcasttranscoder.cli.parsers;

import dk.statsbiblioteket.broadcasttranscoder.cli.MarkAsCompleteContext;
import dk.statsbiblioteket.broadcasttranscoder.cli.OptionParseException;
import dk.statsbiblioteket.broadcasttranscoder.cli.UsageException;
import dk.statsbiblioteket.broadcasttranscoder.persistence.entities.TranscodingRecord;
import dk.statsbiblioteket.util.Pair;
import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.text.*;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/11/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarkAsCompleteOptionsParser<T extends TranscodingRecord> extends InfrastructureOptionsParser<T> {



    protected static final Option PROGRAMS = new Option("programList", true, "The list of programns to analyze");


    private MarkAsCompleteContext<T> context;


    public MarkAsCompleteOptionsParser() {
        super();
        context = new MarkAsCompleteContext<T>();
        getOptions().addOption(PROGRAMS);
    }

    public MarkAsCompleteContext<T> parseOptions(String[] args) throws OptionParseException, UsageException {
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
        try {
            readInfrastructureProperties(context);
       } catch (IOException e) {
            throw new OptionParseException("Error reading properties.", e);
        }
        return context;
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

                line = line.trim();
                if (line.isEmpty()){
                    continue;
                }
                String[] splits = line.split("[, ]");
                String pid = splits[0];
                pid = pid.replace("info:fedora/","");
                String timestamp = splits[1];


                Pair<String, Long> pair = new Pair<String, Long>(pid, parseDate(timestamp));
                context.getRecords().add(pair);
            }
        } catch (FileNotFoundException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not a file.");
        } catch (IOException e) {
            throw new OptionParseException(configFile.getAbsolutePath() + " is not readable.");
        } catch (java.text.ParseException e) {
            throw new OptionParseException("Failed to parse date",e);
        }
    }

    private Long parseDate(String timestamp) throws java.text.ParseException {
        String normalizedTimestamp = normalizeFedoraDate(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return dateFormat.parse(normalizedTimestamp).getTime();
    }

    private String normalizeFedoraDate(String lastModifiedFedoraDate) {
        if (lastModifiedFedoraDate.matches(".*\\.d{3}Z$")){
            return lastModifiedFedoraDate;
        } else if (lastModifiedFedoraDate.matches(".*\\.\\d{2}Z$")){
            return lastModifiedFedoraDate.substring(0,lastModifiedFedoraDate.length()-1)+"0Z";
        }else if (lastModifiedFedoraDate.matches(".*\\.\\d{1}Z$")){
            return lastModifiedFedoraDate.substring(0,lastModifiedFedoraDate.length()-1)+"00Z";
        }else if (lastModifiedFedoraDate.matches(".*:\\d\\dZ$")){
            return lastModifiedFedoraDate.substring(0,lastModifiedFedoraDate.length()-1)+".000Z";
        }
        return lastModifiedFedoraDate;
    }

    @Override
    protected MarkAsCompleteContext<T> getContext() {
        return context;
    }


}
