package ordering.application.exception;

public class ForbiddenOrderAccessException extends RuntimeException {
    public ForbiddenOrderAccessException(String userId, String orderId) {
        super("User " + userId + " is not allowed to report order " + orderId);
    }
}