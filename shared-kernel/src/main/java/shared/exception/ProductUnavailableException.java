package shared.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(String productId) {
        super("Product with ID " + productId + " is not available");
    }
}
