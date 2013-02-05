package dk.statsbiblioteket.broadcasttranscoder;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 2/5/13
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class LockException extends Exception {
    public LockException() {
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockException(Throwable cause) {
        super(cause);
    }

    public LockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
