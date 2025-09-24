package shared.config.security;

public interface JwtUtil {

    String generateAccessToken(String email, String role, String userId);

    String generateRefreshToken(String email);

    boolean validateRefreshToken(String token);

    boolean validateStoredRefreshToken(String token, String email);

    String extractEmailFromRefreshToken(String token);

    String extractEmailFromAccessToken(String token);

    String extractUserIdFromAccessToken(String token);

    String extractRoleFromAccessToken(String token);

    boolean validateAccessToken(String token);
}
