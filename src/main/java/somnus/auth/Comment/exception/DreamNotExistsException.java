package somnus.auth.Comment.exception;

public class DreamNotExistsException extends Exception{
    public DreamNotExistsException() {
    }

    public DreamNotExistsException(String message) {
        super(message);
    }

    public DreamNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DreamNotExistsException(Throwable cause) {
        super(cause);
    }

    public DreamNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
