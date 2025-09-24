package user.application.exception;

public class ZoneCapacityExceededException extends RuntimeException {
    public ZoneCapacityExceededException(String message) {
        super(message);
    }
}
