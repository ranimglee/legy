package restaurant.application.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(String restaurantId) {
        super("Restaurant not found: " + restaurantId);
    }
}
