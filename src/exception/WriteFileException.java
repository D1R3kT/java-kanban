package exception;

public class WriteFileException extends RuntimeException {
    public WriteFileException(String message, Exception exp) {
        super(message, exp);
    }
}
