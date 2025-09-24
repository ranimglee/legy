package payment.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Component
public class JwtConfiguration {

    private static final String SECRET_KEY = "YourVeryLongSecretKeyForJWTMustBeAtLeast32CharactersLong";

    private final Key accessKey = Keys.hmacShaKeyFor(
            Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()).getBytes()
    );

    public String extractUserIdFromAccessToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
    }

    public String extractEmailFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
