package user;

/*
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;
import user.application.dto.in.ClientResponse;
import user.application.dto.out.RegisterClientRequest;
import user.application.exception.EmailAlreadyExistsException;
import user.application.service.ClientService;
import user.application.service.EmailService;
import user.domain.model.ClientEntity;
import user.domain.model.VerificationToken;
import user.domain.repository.UserRepository;
import user.domain.repository.VerificationTokenRepository;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterClientSuccessfully() {
        // Given
        RegisterClientRequest request = new RegisterClientRequest(
                "johndoe", "John", "Doe", "john@example.com",
                "securePass123", "123456789", "123 Main St",
                null, null, null
        );

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByPhoneNumber("123456789")).thenReturn(false);
        when(passwordEncoder.encode("securePass123")).thenReturn("encodedPass");

        ClientEntity savedClient = new ClientEntity();
        savedClient.setId("client-id");
        savedClient.setUsername("johndoe");
        savedClient.setFirstname("John");
        savedClient.setLastname("Doe");
        savedClient.setEmail("john@example.com");
        savedClient.setPhoneNumber("123456789");
        savedClient.setAddress("123 Main St");

        when(userRepository.saveClient(any(ClientEntity.class))).thenReturn(savedClient);

        // When
        ClientResponse response = clientService.registerClient(request);

        // Then
        assertEquals("johndoe", response.username());
        assertEquals("John", response.firstname());
        verify(emailService).sendEmailConfirmation(eq("john@example.com"), eq("johndoe"), anyString());
        verify(verificationTokenRepository).save(any(VerificationToken.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        RegisterClientRequest request = new RegisterClientRequest(
                "johndoe", "John", "Doe", "john@example.com",
                "securePass123", "123456789", "123 Main St",
                null, null, null
        );

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new ClientEntity()));

        assertThrows(EmailAlreadyExistsException.class, () -> clientService.registerClient(request));
    }

    @Test
    void shouldThrowExceptionWhenPhoneExists() {
        RegisterClientRequest request = new RegisterClientRequest(
                "johndoe", "John", "Doe", "john@example.com",
                "securePass123", "123456789", "123 Main St",
                null, null, null
        );

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByPhoneNumber("123456789")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> clientService.registerClient(request));
    }

    @Test
    void shouldRegisterClientWithNullLocation() {
        RegisterClientRequest request = new RegisterClientRequest(
                "janedoe", "Jane", "Doe", "jane@example.com",
                "securePass", "987654321", "456 Street",
                null, null, null
        );

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");

        ClientEntity savedClient = new ClientEntity();
        savedClient.setId("id");
        savedClient.setUsername("janedoe");
        savedClient.setFirstname("Jane");
        savedClient.setLastname("Doe");
        savedClient.setEmail("jane@example.com");
        savedClient.setPhoneNumber("987654321");

        when( userRepository.saveClient(any())).thenReturn(savedClient);

        ClientResponse response = clientService.registerClient(request);
        assertEquals("janedoe", response.username());
        assertEquals("Jane", response.firstname());
        assertNull(response.longitude());
        assertNull(response.latitude());
    }
}
*/