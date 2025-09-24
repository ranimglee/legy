package restaurant.application.exception;

public class ProductFavoriteNotFoundException extends RuntimeException {
    public ProductFavoriteNotFoundException(String userId, String productId) {
        super("Favorite not found for user " + userId + " and product " + productId);
    }
}
