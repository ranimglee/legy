package user.application.exception;

public class FirstLoginPasswordChangeRequiredException extends RuntimeException {
    public FirstLoginPasswordChangeRequiredException(String message) {
        super(message);
    }
}