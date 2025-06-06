package common.exception;

public class CouldNotSendException extends RuntimeException {
    public CouldNotSendException(String message) {
        super(message);
    }
}
