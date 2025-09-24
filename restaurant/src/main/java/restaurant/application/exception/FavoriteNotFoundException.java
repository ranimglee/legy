package restaurant.application.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(String userId, String restaurantId) {
        super("Favorite not found for user " + userId + " and restaurant " + restaurantId);
    }
}