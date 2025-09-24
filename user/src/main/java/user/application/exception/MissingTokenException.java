package user.application.exception;

public class MissingTokenException extends RuntimeException {
    public MissingTokenException() {
        super("Authorization header is missing or malformed");
    }
}