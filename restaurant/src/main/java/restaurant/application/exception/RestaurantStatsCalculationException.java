package restaurant.application.exception;

public class RestaurantStatsCalculationException extends RuntimeException {

    public RestaurantStatsCalculationException(String message) {
        super(message);
    }

    public RestaurantStatsCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
