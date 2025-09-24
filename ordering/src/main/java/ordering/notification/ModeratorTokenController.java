package ordering.notification;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.domain.model.UserToken;
import ordering.domain.repository.ModeratorTokenRepository;
import ordering.domain.repository.UserTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shared.config.security.JwtUtil;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ModeratorTokenController {

    private final ModeratorTokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final UserTokenService userTokenService;

    @PostMapping("/moderateur/fcm-token")
    public ResponseEntity<Void> registerToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody String fcmToken
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        if (fcmToken == null || fcmToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String userId = jwtUtil.extractUserIdFromAccessToken(authHeader.replace("Bearer ", ""));
            tokenRepository.saveToken(userId, fcmToken);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Failed to save FCM token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/client/fcm-token-client")
    public ResponseEntity<Void> registerClientToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody String fcmToken
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            return ResponseEntity.badRequest().build();
        }
        if (fcmToken == null || fcmToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String userId = jwtUtil.extractUserIdFromAccessToken(authHeader.replace("Bearer ", ""));
            userTokenService.saveToken(userId, fcmToken);
            log.info("FCM token saved for client: {}", userId);
            return ResponseEntity.ok().build();
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RuntimeException e) {
            log.error("Failed to save FCM token for client: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
