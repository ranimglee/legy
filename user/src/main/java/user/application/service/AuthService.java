package user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user.application.dto.in.LoginResponse;
import user.application.dto.out.ChangePasswordRequest;
import user.application.dto.out.LoginRequest;
import user.application.exception.*;
import user.domain.model.*;
import user.domain.repository.UserRepository;
import user.infrastructure.security.JwtUtilImpl;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilImpl jwtUtil;
    private final EmailService emailService;
    private final ResetCodeService resetCodeService;
    private final WhatsAppService whatsAppService;

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 10 * 60 * 1000;

    private final ConcurrentHashMap<String, FailedLoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    public LoginResponse login(LoginRequest request) {

        Optional<UserEntity> userOptional = userRepository.findByEmail(request.email());

        if (userOptional.isEmpty()) {
            recordFailedAttempt(request.email());
            throw new InvalidCredentialsException("Invalid email or password!");
        }

        UserEntity user = userOptional.get();
        String email = user.getEmail();

        if (user.getStatus() == Status.BANNED) {
            throw new AccessDeniedException("Your account is banned.");
        }

        if (isAccountLocked(email)) {
            throw new UserLockedException("Too many failed attempts. Try again later.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            recordFailedAttempt(email);
            throw new InvalidCredentialsException("Invalid email or password!");
        }

        if (!user.getStatus().equals(Status.ACTIVE)) {
            throw new UserNotActiveException("Your account is not active. Please verify your email.");
        }


        loginAttempts.remove(email);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.saveUser(user);


        return new LoginResponse(accessToken, refreshToken, user.getRole());
    }

    private void recordFailedAttempt(String email) {
        loginAttempts.put(email, loginAttempts.getOrDefault(email, new FailedLoginAttempt()).increment());
    }

    private boolean isAccountLocked(String email) {
        if (!loginAttempts.containsKey(email)) return false;

        FailedLoginAttempt attempt = loginAttempts.get(email);
        return attempt.isLocked() && attempt.getLockTime() + LOCK_TIME > System.currentTimeMillis();
    }

    private static class FailedLoginAttempt {
        private int attempts;
        private long lockTime;

        public FailedLoginAttempt increment() {
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                lockTime = System.currentTimeMillis();
            }
            return this;
        }

        public boolean isLocked() {
            return attempts >= MAX_ATTEMPTS;
        }

        public long getLockTime() {
            return lockTime;
        }
    }

    public void sendResetCode(String email, String channel) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getStatus().equals(Status.ACTIVE)) return;

        UserEntity user = userOpt.get();
        try {
            String code = resetCodeService.generateCode(email);
            if ("whatsapp".equalsIgnoreCase(channel)) {
                if (user.getPhoneNumber() == null) {
                    throw new MissingPhoneNumberException("No phone number available for WhatsApp.");
                }
                whatsAppService.sendResetCode(user.getPhoneNumber(), code);
            } else {
                emailService.sendResetPasswordCode(user.getEmail(), code);
            }
        } catch (Exception e) {
            throw new ResetCodeDeliveryException("Failed to send reset code for user: " + user.getEmail(), e);
        }
    }

    public void resetPassword(String email, String code, String newPassword) {
        if (!resetCodeService.validateCode(email, code)) {
            throw new InvalidResetCodeException("Invalid or expired verification code.");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidResetCodeException("Invalid request."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.saveUser(user);
        resetCodeService.deleteCode(email);
    }

    public void changePassword(String token, String currentPassword, String newPassword) {
        String email = jwtUtil.extractEmailFromAccessToken(token);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found."));

        // Check if the current password matches the stored password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.saveUser(user);
    }





}