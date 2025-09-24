package ordering.application.exception;

public class PromoCodeNotFoundException extends RuntimeException {
    public PromoCodeNotFoundException(String id) {
        super("Promo code not found with ID: " + id);
    }
}
