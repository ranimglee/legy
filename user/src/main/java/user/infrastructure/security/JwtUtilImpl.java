package user.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import shared.config.security.JwtUtil;
import user.domain.model.UserEntity;
import user.domain.repository.UserRepository;
import java.util.function.Function;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtilImpl implements JwtUtil {
    private final UserRepository userRepository;

    private static final String SECRET_KEY = "YourVeryLongSecretKeyForJWTMustBeAtLeast32CharactersLong";
    private static final String REFRESH_SECRET_KEY = "YourVeryLongRefreshTokenSecretKeyForJWT";
    private static final long ACCESS_EXPIRATION_TIME = 864000000; // 150 min
    private static final long REFRESH_EXPIRATION_TIME = 604800000; // 7 days

    private final Key accessKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private final Key refreshKey = Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());

    public JwtUtilImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String generateAccessToken(String email, String role, String userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", "ROLE_" + role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public boolean validateStoredRefreshToken(String token, String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) return false;

        UserEntity user = userOptional.get();
        return token.equals(user.getRefreshToken());
    }


    public String extractEmailFromRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractEmailFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public String extractUserIdFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
    }

    public String extractRoleFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }


    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
