package dk.statsbiblioteket.broadcasttranscoder.util;

import java.io.File;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/4/13
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockFile extends File {
    private long length;

    public MockFile(String pathname, long length) {
        super(pathname);
        this.length = length;
    }

    @Override
    public long length() {
        return length;
    }


}
