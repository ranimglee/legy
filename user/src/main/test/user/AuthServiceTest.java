package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import user.application.dto.in.LoginResponse;
import user.application.dto.out.LoginRequest;
import user.application.exception.*;
import user.application.service.AuthService;
import user.application.service.EmailService;
import user.application.service.ResetCodeService;
import user.application.service.WhatsAppService;
import user.domain.model.*;
import user.domain.repository.UserRepository;
import user.infrastructure.security.JwtUtilImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtilImpl jwtUtil;
    @Mock private EmailService emailService;
    @Mock private ResetCodeService resetCodeService;
    @Mock private WhatsAppService whatsAppService;

    @InjectMocks private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil, emailService, resetCodeService, whatsAppService);
    }

    private ClientEntity createActiveClient(String passwordHash) {
        ClientEntity client = new ClientEntity();
        client.setId("user-id");
        client.setEmail("test@example.com");
        client.setPassword(passwordHash);
        client.setStatus(Status.ACTIVE);
        client.setPhoneNumber("+123456789");
        return client;
    }

    @Test
    void shouldLoginSuccessfully() {
        String email = "test@example.com";
        String password = "Password123!";
        String hashed = "hashed-password";

        ClientEntity user = createActiveClient(hashed);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashed)).thenReturn(true);
        when(jwtUtil.generateAccessToken(email, "CLIENT", "user-id")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(email)).thenReturn("refresh-token");

        LoginRequest request = new LoginRequest(email, password);
        LoginResponse response = authService.login(request);

        assertEquals("access-token", response.token());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("CLIENT", response.role());

        verify(userRepository).saveUser(user);
    }

    @Test
    void shouldThrowIfUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("unknown@example.com", "pass");

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowIfPasswordInvalid() {
        String email = "test@example.com";
        ClientEntity user = createActiveClient("hashed");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        LoginRequest request = new LoginRequest(email, "wrong");

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowIfUserBanned() {
        ClientEntity banned = createActiveClient("hashed");
        banned.setStatus(Status.BANNED);

        when(userRepository.findByEmail(banned.getEmail())).thenReturn(Optional.of(banned));

        LoginRequest request = new LoginRequest(banned.getEmail(), "pass");

        assertThrows(AccessDeniedException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowIfUserInactive() {
        ClientEntity inactive = createActiveClient("hashed");
        inactive.setStatus(Status.PENDING);

        when(userRepository.findByEmail(inactive.getEmail())).thenReturn(Optional.of(inactive));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);

        LoginRequest request = new LoginRequest(inactive.getEmail(), "pass");

        assertThrows(UserNotActiveException.class, () -> authService.login(request));
    }

    @Test
    void shouldSendResetCodeByEmail() {
        ClientEntity user = createActiveClient("pass");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(resetCodeService.generateCode(user.getEmail())).thenReturn("123456");

        authService.sendResetCode(user.getEmail(), "email");

        verify(emailService).sendResetPasswordCode(eq(user.getEmail()), eq("123456"));
    }

    @Test
    void shouldSendResetCodeByWhatsApp() {
        ClientEntity user = createActiveClient("pass");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(resetCodeService.generateCode(user.getEmail())).thenReturn("123456");

        authService.sendResetCode(user.getEmail(), "whatsapp");

        verify(whatsAppService).sendResetCode(eq(user.getPhoneNumber()), eq("123456"));
    }

    @Test
    void shouldSendResetCodeViaEmail() {
        ClientEntity user = createActiveClient("hashed");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(resetCodeService.generateCode(user.getEmail())).thenReturn("123456");

        authService.sendResetCode(user.getEmail(), "email");

        verify(emailService).sendResetPasswordCode(eq(user.getEmail()), eq("123456"));
    }

    @Test
    void shouldSendResetCodeViaWhatsApp() {
        ClientEntity user = createActiveClient("hashed");
        user.setPhoneNumber("+123456789");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(resetCodeService.generateCode(user.getEmail())).thenReturn("123456");

        authService.sendResetCode(user.getEmail(), "whatsapp");

        verify(whatsAppService).sendResetCode(eq(user.getPhoneNumber()), eq("123456"));
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        String email = "test@example.com";
        ClientEntity user = createActiveClient("old-hash");

        when(resetCodeService.validateCode(email, "code123")).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("new-hash");

        authService.resetPassword(email, "code123", "newPass");

        verify(userRepository).saveUser(user);
        verify(resetCodeService).deleteCode(email);
        assertEquals("new-hash", user.getPassword());
    }

    @Test
    void shouldThrowOnInvalidResetCode() {
        when(resetCodeService.validateCode("email", "bad-code")).thenReturn(false);

        assertThrows(InvalidResetCodeException.class, () ->
                authService.resetPassword("email", "bad-code", "pass")
        );
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        ClientEntity user = createActiveClient("old-hash");
        String token = "valid.jwt.token";

        when(jwtUtil.extractEmailFromAccessToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("new-hash");

        authService.changePassword(token, "old", "new");

        verify(userRepository).saveUser(user);
        assertEquals("new-hash", user.getPassword());
    }

    @Test
    void shouldThrowIfCurrentPasswordWrong() {
        ClientEntity user = createActiveClient("hashed");
        String token = "token";

        when(jwtUtil.extractEmailFromAccessToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () ->
                authService.changePassword(token, "wrong", "new")
        );
    }

}
