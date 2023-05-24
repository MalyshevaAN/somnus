package somnus.auth.Dream.exception;

public class DreamNotFoundException extends Exception{
    public DreamNotFoundException() {
    }

    public DreamNotFoundException(String message) {
        super(message);
    }

    public DreamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DreamNotFoundException(Throwable cause) {
        super(cause);
    }

    public DreamNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
