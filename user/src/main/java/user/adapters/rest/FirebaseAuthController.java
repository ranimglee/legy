package user.adapters.rest;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user.application.dto.in.FirebaseLoginRequest;
import user.application.dto.in.FirebaseLoginResponse;
import user.application.dto.in.LoginResponse;
import user.application.exception.InvalidFirebaseTokenException;
import user.application.service.ClientService;
import user.domain.model.*;
import user.domain.repository.UserRepository;
import user.infrastructure.security.JwtUtilImpl;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Handles Firebase login via Google")
public class FirebaseAuthController {

    private final ClientService clientService;
    private final UserRepository userRepository;
    private final JwtUtilImpl jwtUtil;

    @Operation(
            summary = "Login with Firebase ID token",
            description = "Authenticates a user via Google Sign-In using a Firebase ID token"
    )

    @PostMapping("/firebase/google")
    public ResponseEntity<FirebaseLoginResponse> loginWithFirebase(@RequestBody FirebaseLoginRequest payload) {
        String idToken = payload.idToken();

        if (idToken == null || idToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String email = decoded.getEmail();
            String name = (String) decoded.getClaims().get("name");

            if (email == null) {
                return ResponseEntity.status(400).build();
            }

            UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
                String[] parts = name != null ? name.split(" ", 2) : new String[]{"Firebase", "User"};
                String username = email.split("@")[0] + "-" + UUID.randomUUID().toString().substring(0, 4);
                return clientService.registerClientFromFirebase(
                        email,
                        username,
                        parts[0],
                        parts.length > 1 ? parts[1] : ""
                );
            });

            String token = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            user.setRefreshToken(refreshToken);
            userRepository.saveUser(user);

            boolean missingPhoneNumber = user.getPhoneNumber() == null || user.getPhoneNumber().isBlank();

            FirebaseLoginResponse response = new FirebaseLoginResponse(
                    token,
                    refreshToken,
                    user.getRole(),
                    missingPhoneNumber
            );

            return missingPhoneNumber
                    ? ResponseEntity.unprocessableEntity().body(response)
                    : ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            throw new InvalidFirebaseTokenException("Firebase token verification failed", e);

        }
    }


    @PostMapping("/firebase/apple")
    public ResponseEntity<LoginResponse> loginWithApple(@RequestBody FirebaseLoginRequest payload) {
        String idToken = payload.idToken();
        if (idToken == null || idToken.isBlank()) {
            log.warn("❌ Apple login failed: idToken missing");
            return ResponseEntity.badRequest().build();
        }

        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decoded.getEmail();
            log.info("🍎 Apple sign-in attempt from {}", email);

            if (email == null) {
                log.warn("❌ Apple ID token missing email");
                return ResponseEntity.status(400).body(new LoginResponse(null, null, null));
            }

            UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
                log.info("🆕 Registering new Apple user: {}", email);
                String fallbackName = "Apple User";
                return clientService.registerClientFromFirebase(
                        email,
                        email.split("@")[0] + "-" + UUID.randomUUID().toString().substring(0, 4),
                        fallbackName,
                        ""
                );
            });

            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            user.setRefreshToken(refreshToken);
            userRepository.saveUser(user);

            return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, user.getRole()));
        } catch (FirebaseAuthException e) {
            log.error("❌ Invalid Apple Firebase token: {}", e.getMessage(), e);
            return ResponseEntity.status(401).build();
        }
    }

}
