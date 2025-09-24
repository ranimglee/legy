package user.application.exception;

public class LivreurNotFoundException extends RuntimeException {
    public LivreurNotFoundException(String id) {
        super("Livreur not found with id: " + id);
    }
}
