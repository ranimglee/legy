package restaurant.application.exception;

public class UserPreferenceNotFoundException extends RuntimeException {
    public UserPreferenceNotFoundException(String userId) {
        super("Preferences not found for user " + userId);
    }
}
