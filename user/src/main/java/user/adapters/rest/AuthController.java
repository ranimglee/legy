package user.adapters.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import user.application.dto.in.LoginResponse;
import user.application.dto.in.RequestResetCodeRequest;
import user.application.dto.in.VerifyResetCodeResponse;
import user.application.dto.out.*;
import user.application.exception.InvalidTokenException;
import user.application.exception.MissingTokenException;
import user.application.exception.UserNotFoundException;
import user.application.service.AuthService;
import user.application.service.ResetCodeService;
import user.domain.model.LivreurEntity;
import user.domain.model.UserEntity;
import user.domain.repository.UserRepository;
import user.domain.value.LivreurLocationDTO;
import user.infrastructure.kafka.ClosestLivreur.ClosestLivreurResponseProducer;
import user.infrastructure.security.JwtUtilImpl;


import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "Endpoints for login, logout, password reset, and token refresh")
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtilImpl jwtUtil;
    private final UserRepository userRepository;
    private final ResetCodeService resetCodeService;

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ClosestLivreurResponseProducer.class);
    private static final String FORCE_LOGOUT_PREFIX = "FORCE_LOGOUT:";
    private static final String LIVREUR_SET = "connected_livreurs";
    private static final String SESSION_PREFIX = "WS_SESSION:";
    private static final String LIVREUR_SESSIONS_PREFIX = "livreur_sessions:";



    public AuthController(AuthService authService, JwtUtilImpl jwtUtil, UserRepository userRepository, ResetCodeService resetCodeService, @Qualifier("connectedRedisTemplate") RedisTemplate<String, String> redisTemplate,
                          SimpMessagingTemplate messagingTemplate) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.resetCodeService = resetCodeService;
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;

    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token.");
        }

        String email = jwtUtil.extractEmailFromRefreshToken(refreshToken);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!jwtUtil.validateStoredRefreshToken(refreshToken, email)) {
            throw new InvalidTokenException("Invalid refresh token.");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        return ResponseEntity.ok(new LoginResponse(newAccessToken, refreshToken, user.getRole()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token.");
        }

        String email = jwtUtil.extractEmailFromRefreshToken(refreshToken);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setRefreshToken(null);
        userRepository.saveUser(user);

        return ResponseEntity.ok(Map.of("message", "Logout successful."));
    }

    @GetMapping
    public List<LivreurEntity> getConnectedLivreurs() {
        return userRepository.findAvailableLivreurs(); // Or via a service
    }

    @GetMapping("/connected-livreurs")
    public List<Map<String, String>> getConnected_Livreurs() {
        Set<String> ids = redisTemplate.opsForSet().members("connected_livreurs");
        if (ids == null) return List.of();

        return ids.stream().map(id -> {
            String loc = redisTemplate.opsForValue().get("livreur:" + id + ":location"+ ":status");
            String[] parts = loc != null ? loc.split(",") : new String[]{"-", "-"};
            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("latitude", parts[0]);
            map.put("longitude", parts[1]);
            map.put("status","FREE");
            return map;
        }).collect(Collectors.toList());
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // authService.sendResetCode may throw custom exceptions (e.g. EmailNotFoundException)
        authService.sendResetCode(request.email(), request.channel());
        return ResponseEntity.ok(Map.of("message",
                "If this email is registered, a reset code has been sent."));
    }


    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader(name = "Authorization", required = true) String header,
            @RequestBody ChangePasswordRequest request) {

        String token = extractToken(header);
        authService.changePassword(token, request.currentPassword(), request.newPassword());

        return ResponseEntity.ok(Map.of("message", "Password successfully changed."));
    }

    private String extractToken(String header) {
        if (header == null || header.isBlank()) {
            throw new MissingTokenException();
        }
        if (!header.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid authorization header format.");
        }
        return header.substring(7);
    }

    @MessageMapping("/livreur/force-logout")
    public void forceLogout(SimpMessageHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        if (sessionId == null) {
            logger.warn(" Session ID missing during manual logout");
            return;
        }

        redisTemplate.opsForValue().set(FORCE_LOGOUT_PREFIX + sessionId, "true", Duration.ofSeconds(5));

        String sessionData = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
        if (sessionData == null || !sessionData.contains(":")) {
            logger.warn("⚠️ No valid session data in Redis for session: {}", sessionId);
            return;
        }

        String[] parts = sessionData.split(":");
        if (parts.length != 2) {
            logger.warn("⚠️ Invalid session data: {}", sessionData);
            return;
        }

        String userId = parts[0];
        String role = parts[1].toLowerCase();
        String redisSetKey = switch (role) {
            case "livreur" -> "connected_livreurs";
            case "client" -> "connected_clients";
            case "moderateur" -> "connected_moderateurs";
            case "manager", "restaurantmanager" -> "connected_restaurant_managers";
            case "financier" -> "connected_financiers";
            default -> "connected_unknown";
        };

        // Remove all session bindings for the user
        String userSessionKey = LIVREUR_SESSIONS_PREFIX + userId;
        Set<String> sessionIds = redisTemplate.opsForSet().members(userSessionKey);
        if (sessionIds != null) {
            for (String sid : sessionIds) {
                redisTemplate.opsForValue().set(FORCE_LOGOUT_PREFIX + sid, "true", Duration.ofSeconds(5));
                redisTemplate.delete(SESSION_PREFIX + sid);
            }
        }

        redisTemplate.delete(userSessionKey);
        redisTemplate.opsForSet().remove(LIVREUR_SET, userId);
        redisTemplate.delete("livreur:" + userId + ":location");

        messagingTemplate.convertAndSend("/topic/livreurs/disconnected", userId);
        logger.info("🚪 Manual logout – forcibly disconnected ALL sessions for livreur: {}", userId);
    }

    @MessageMapping("/auth/update-location")
    public void updateLocation(LivreurLocationDTO locationDTO, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String userId = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);

        if (userId != null && locationDTO.getLatitude() != null && locationDTO.getLongitude() != null) {
            String redisLocationKey = "livreur:" + userId + ":location";
            String locationValue = locationDTO.getLatitude() + "," + locationDTO.getLongitude();
            redisTemplate.opsForValue().set(redisLocationKey, locationValue);
            logger.info("📍 Updated livreur {} location in Redis: {}", userId, locationValue);

            LivreurLocationDTO locationWithId = new LivreurLocationDTO(
                    userId,
                    locationDTO.getLatitude(),
                    locationDTO.getLongitude()
            );

            // 🔄 Diffusion globale (facultatif)
            //messagingTemplate.convertAndSend("/topic/livreurs/location", locationWithId);

            // 🔐 Diffusion individuelle au livreur spécifique
            messagingTemplate.convertAndSend("/topic/livreurs/" + userId + "/location", locationWithId);
        }
    }



    @PostMapping("/request-reset")
    public ResponseEntity<GenericMessageResponse> requestReset(@RequestBody RequestResetCodeRequest request) {
        authService.sendResetCode(request.email(), request.channel());
        return ResponseEntity.ok(new GenericMessageResponse("Le code a été envoyé à votre adresse e-mail."));
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<VerifyResetCodeResponse> verifyResetCode(@RequestBody VerifyResetCodeRequest request) {
        boolean valid = resetCodeService.validateCode(request.email(), request.code());
        return ResponseEntity.ok(new VerifyResetCodeResponse(valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Password successfully reset."));
    }

}

