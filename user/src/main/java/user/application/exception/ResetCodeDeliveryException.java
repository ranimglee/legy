package user.application.exception;

public class ResetCodeDeliveryException extends RuntimeException {
    public ResetCodeDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
