package restaurant.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Component
public class JwtConfig {
    private static final String SECRET_KEY = "YourVeryLongSecretKeyForJWTMustBeAtLeast32CharactersLong";
    private final Key accessKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    public String extractUserIdFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
    }
}
