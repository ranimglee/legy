package user.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import user.domain.model.AuthProvider;
import user.domain.model.ClientEntity;
import user.domain.model.Status;
import user.domain.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtilImpl jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        String email = principal.getAttribute("email");
        if (email == null) {
            email = "facebook_" + principal.getAttribute("id") + "@placeholder.com";
        }

        String firstname = String.valueOf(Optional.ofNullable(principal.getAttribute("given_name"))
                .orElse(principal.getAttribute("first_name")));
        String lastname = String.valueOf(Optional.ofNullable(principal.getAttribute("family_name"))
                .orElse(principal.getAttribute("last_name")));

        AuthProvider provider = email.contains("facebook") ? AuthProvider.FACEBOOK : AuthProvider.GOOGLE;

        String finalEmail = email;
        ClientEntity client = (ClientEntity) userRepository.findByEmail(email)
                .orElseGet(() -> {
                    ClientEntity newClient = ClientEntity.builder()
                            .username(finalEmail.split("@")[0] + "-" + UUID.randomUUID().toString().substring(0, 4))
                            .firstname(firstname)
                            .lastname(lastname)
                            .email(finalEmail)
                            .status(Status.ACTIVE)
                            .provider(provider)
                            .build();
                    return userRepository.saveUser(newClient);
                });

        String accessToken = jwtUtil.generateAccessToken(client.getEmail(), client.getRole(), client.getId());
        String refreshToken = jwtUtil.generateRefreshToken(client.getEmail());

        client.setRefreshToken(refreshToken);
        userRepository.saveUser(client);

        response.setContentType("application/json");
        response.getWriter().write("{"
                + "\"accessToken\": \"" + accessToken + "\","
                + "\"refreshToken\": \"" + refreshToken + "\","
                + "\"role\": \"" + client.getRole() + "\""
                + "}");
    }
}
