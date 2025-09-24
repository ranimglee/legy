package restaurant.application.exception;

public class InvalidPromotionDatesException extends RuntimeException {
    public InvalidPromotionDatesException(String message) {
        super(message);
    }
}
