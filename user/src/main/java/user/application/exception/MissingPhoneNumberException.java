package user.application.exception;

public class MissingPhoneNumberException extends RuntimeException {
    public MissingPhoneNumberException(String message) {
        super(message);
    }
}
