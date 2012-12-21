package dk.statsbiblioteket.broadcasttranscoder.cli;

import java.io.File;
import java.util.Properties;

public  abstract class AbstractOptionsParser {
    public AbstractOptionsParser() {
    }

    protected File readFileProperty(String propName, Properties props)  throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        return new File(prop);
    }

    protected File readExistingDirectoryProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        File dir = new File(prop);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new OptionParseException(dir.getAbsolutePath() + " must be a pre-existing directory");
        }
        return dir;
    }

    protected String readStringProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        if (prop == null || "".equals(prop)) {
            throw new OptionParseException("Property " + propName + " not set.");
        }
        return prop;
    }

    protected int readIntegerProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Integer result = Integer.parseInt(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as an integer.");
        }
    }

    protected long readLongProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            Long result = Long.parseLong(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as a long.");
        }
    }

    protected boolean readBooleanProperty(String propName, Properties props) throws OptionParseException {
        String prop = props.getProperty(propName);
        try {
            boolean result = Boolean.parseBoolean(prop);
            return result;
        } catch (NumberFormatException e) {
            throw new OptionParseException("Cannot parse " + prop + " as a boolean.");
        }
    }

}