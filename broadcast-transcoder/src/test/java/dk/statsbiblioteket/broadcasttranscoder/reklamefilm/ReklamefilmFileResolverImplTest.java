package dk.statsbiblioteket.broadcasttranscoder.reklamefilm;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 29/01/13
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ReklamefilmFileResolverImplTest {

    @Test
    public void testGetFile() throws URISyntaxException {
        SingleTranscodingContext context = new SingleTranscodingContext();

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource("dk/statsbiblioteket/broadcasttranscoder/reklamefilm/data/dir1/file1");
        String datadir = new File(resource.toURI()).getParentFile().getParentFile().getAbsolutePath();
        context.setReklamefileRootDirectories(new String[]{datadir});
        ReklamefilmFileResolverImpl resolver = new ReklamefilmFileResolverImpl(context);
        File foundFile = resolver.getFile("foobar", "file1");
        assertTrue(foundFile.exists());
    }

}
