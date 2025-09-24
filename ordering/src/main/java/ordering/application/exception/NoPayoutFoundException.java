package ordering.application.exception;


public class NoPayoutFoundException extends RuntimeException {
    public NoPayoutFoundException(String restaurantId) {
        super("No payout found for restaurant with ID: " + restaurantId);
    }
}

