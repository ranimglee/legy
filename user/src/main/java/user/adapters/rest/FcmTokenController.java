package user.adapters.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shared.config.security.JwtUtil;
import user.domain.repository.UserRepository;

import java.util.Map;
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")

@Slf4j
public class FcmTokenController {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/driver/fcm-token")
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
            userRepository.updateFcmToken(userId, fcmToken);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Failed to save FCM token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
