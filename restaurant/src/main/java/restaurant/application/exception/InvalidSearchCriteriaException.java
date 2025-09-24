package restaurant.application.exception;

public class InvalidSearchCriteriaException extends RuntimeException {
    public InvalidSearchCriteriaException(String message) {
        super("Invalid search criteria: " + message);
    }
}