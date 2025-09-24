package restaurant.application.exception;

public class DuplicateFavoriteException extends RuntimeException {
    public DuplicateFavoriteException(String userId, String restaurantId) {
        super("User " + userId + " has already favorited restaurant " + restaurantId);
    }
}
