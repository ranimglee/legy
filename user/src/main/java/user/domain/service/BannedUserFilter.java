package user.domain.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import user.domain.model.Status;
import user.domain.repository.UserRepository;
import user.infrastructure.security.JwtUtilImpl;

import java.io.IOException;

@Component
public class BannedUserFilter extends OncePerRequestFilter {

    private final JwtUtilImpl jwtUtil;
    private final UserRepository userRepository;

    public BannedUserFilter(JwtUtilImpl jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = jwtUtil.extractEmailFromAccessToken(token);
                userRepository.findByEmail(email).ifPresent(user -> {
                    if (user.getStatus() == Status.BANNED) {
                        throw new AccessDeniedException("⛔ Your account has been banned.");
                    }
                });
            } catch (Exception ignored) {
                // let JwtAuthenticationFilter handle token validation errors
            }
        }

        filterChain.doFilter(request, response);
    }
}
