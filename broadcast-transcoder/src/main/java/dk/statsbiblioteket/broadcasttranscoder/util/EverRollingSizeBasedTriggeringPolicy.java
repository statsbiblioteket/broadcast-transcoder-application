package dk.statsbiblioteket.broadcasttranscoder.util;

import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 6/3/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class EverRollingSizeBasedTriggeringPolicy<E> extends SizeBasedTriggeringPolicy<E> {

    /**
     * The default maximum file size.
     */
    public static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    String maxFileSizeAsString = Long.toString(DEFAULT_MAX_FILE_SIZE);
    FileSize maxFileSize;


    public EverRollingSizeBasedTriggeringPolicy() {
    }

    public EverRollingSizeBasedTriggeringPolicy(final String maxFileSize) {
        setMaxFileSize(maxFileSize);
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        return (activeFile.length() >= maxFileSize.getSize());
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSizeAsString = maxFileSize;
        this.maxFileSize = FileSize.valueOf(maxFileSize);
    }
}
