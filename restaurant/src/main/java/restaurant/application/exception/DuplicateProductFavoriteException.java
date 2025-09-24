package restaurant.application.exception;

public class DuplicateProductFavoriteException extends RuntimeException {
    public DuplicateProductFavoriteException(String userId, String productId) {
        super("User " + userId + " has already favorited product " + productId);
    }
}
