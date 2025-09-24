package user.adapters.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import user.application.dto.in.LoginResponse;
import user.domain.model.AuthProvider;
import user.domain.model.ClientEntity;
import user.domain.model.Status;
import user.domain.model.UserEntity;
import user.domain.repository.UserRepository;
import user.infrastructure.security.JwtUtilImpl;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2SuccessController {

    private final UserRepository userRepository;
    private final JwtUtilImpl jwtUtil;

    @GetMapping("/success")
    public ResponseEntity<LoginResponse> success(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");

        // Fallback email generation (temporary)
        if (email == null) {
            email = "facebook_" + principal.getAttribute("id") + "@placeholder.com";
        }

        String firstname = principal.getAttribute("given_name"); // Google
        String lastname = principal.getAttribute("family_name"); // Google

        // Fallback to Facebook attributes if null
        if (firstname == null) {
            firstname = principal.getAttribute("first_name"); // Facebook
        }
        if (lastname == null) {
            lastname = principal.getAttribute("last_name"); // Facebook
        }

        // Determine provider
        AuthProvider provider = getProvider(principal);

        Optional<UserEntity> existingUser = userRepository.findByEmail(email);

        ClientEntity client;
        if (existingUser.isPresent()) {
            client = (ClientEntity) existingUser.get();
        } else {
            client = ClientEntity.builder()
                    .username(generateUsername(email))
                    .firstname(firstname)
                    .lastname(lastname)
                    .email(email)
                    .status(Status.ACTIVE)
                    .provider(provider)
                    .build();

            userRepository.saveUser(client);
        }

        String accessToken = jwtUtil.generateAccessToken(client.getEmail(), client.getRole(), client.getId());
        String refreshToken = jwtUtil.generateRefreshToken(client.getEmail());

        client.setRefreshToken(refreshToken);
        userRepository.saveUser(client);

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, client.getRole()));
    }

    private AuthProvider getProvider(OAuth2User principal) {
        String registrationId = principal.getAttribute("iss") != null ? "google" : "facebook";
        return registrationId.equals("google") ? AuthProvider.GOOGLE : AuthProvider.FACEBOOK;
    }

    private String generateUsername(String email) {
        return email.split("@")[0] + "-" + UUID.randomUUID().toString().substring(0, 4);
    }
}
