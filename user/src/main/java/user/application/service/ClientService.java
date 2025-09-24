package user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user.application.dto.in.ClientProfileResponse;
import user.application.dto.in.ClientResponse;
import user.application.dto.out.RegisterClientRequest;
import user.application.dto.out.UpdateClientProfileRequest;
import user.application.exception.EmailAlreadyExistsException;
import user.application.exception.PhoneNumberAlreadyExistsException;
import user.application.exception.UserNotFoundException;
import user.domain.model.*;
import user.domain.repository.UserRepository;
import user.domain.repository.VerificationTokenRepository;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ClientResponse registerClient(RegisterClientRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }

        String rawPassword = request.password();

        ClientEntity client = new ClientEntity();
        client.setUsername(request.username());
        client.setFirstname(request.firstname());
        client.setLastname(request.lastname());
        client.setEmail(request.email());
        client.setPassword(passwordEncoder.encode(rawPassword));
        client.setPhoneNumber(request.phoneNumber());
        client.setAddress(request.address());
        client.setStatus(Status.PENDING);
        client.setGuestSessionId(request.guestSessionId());
        client.setLongitude(request.longitude());
        client.setLatitude(request.latitude());
        client.setCreatedAt(Instant.now());
        log.info("Registering client with guestSessionId = {}", request.guestSessionId());

        client = userRepository.saveClient(client);

        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(client.getId());
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(token);

        String confirmationLink = "https://api.dev.legy.bramasquare.com/api/users/verify-email?token=" + token.getToken();
        emailService.sendEmailConfirmation(client.getEmail(), client.getUsername(), confirmationLink);

        return new ClientResponse(
                client.getUsername(),
                client.getFirstname(),
                client.getLastname(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getAddress(),
                client.getLongitude(),
                client.getLatitude(),
                client.getCreatedAt()
        );
    }


    public boolean verifyEmail(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken.isEmpty() || verificationToken.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        Optional<UserEntity> clientOptional = userRepository.findById(verificationToken.get().getUserId());
        if (clientOptional.isEmpty()) {
            return false;
        }

        UserEntity client = clientOptional.get();
        client.setStatus(Status.ACTIVE);
        userRepository.saveClient((ClientEntity) client);

        verificationTokenRepository.delete(verificationToken.get());
        return true;
    }

    public ClientProfileResponse getClientByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!(user instanceof ClientEntity)) {
            throw new UserNotFoundException("User is not a client");
        }

        ClientEntity client = (ClientEntity) user;

        return ClientProfileResponse.builder()
                .id(client.getId())
                .username(client.getUsername())
                .firstname(client.getFirstname())
                .lastname(client.getLastname())
                .email(client.getEmail())
                .phoneNumber(client.getPhoneNumber())
                .address(client.getAddress())
                .longitude(client.getLongitude())
                .latitude(client.getLatitude())
                .build();
    }

    public ClientProfileResponse updateProfile(String email, UpdateClientProfileRequest request) {
        ClientEntity client = (ClientEntity) userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        if (request.firstname() != null) {
            client.setFirstname(request.firstname());
        }
        if (request.lastname() != null) {
            client.setLastname(request.lastname());
        }
        if (request.phoneNumber() != null) {
            client.setPhoneNumber(request.phoneNumber());
        }
        if (request.address() != null) {
            client.setAddress(request.address());
        }

        userRepository.saveUser(client);

        return ClientProfileResponse.builder()
                .id(client.getId())
                .username(client.getUsername())
                .firstname(client.getFirstname())
                .lastname(client.getLastname())
                .email(client.getEmail())
                .phoneNumber(client.getPhoneNumber())
                .address(client.getAddress())
                .longitude(client.getLongitude())
                .latitude(client.getLatitude())
                .build();
    }

    public ClientEntity registerClientFromFirebase(String email, String username, String firstname, String lastname) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        ClientEntity client = ClientEntity.builder()
                .username(username)
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .status(Status.ACTIVE)
                .provider(AuthProvider.GOOGLE)
                .build();


        return userRepository.saveClient(client);
    }
    public Map<String, Long> getMonthlyClientEvolution() {
        log.info("📊 Calculating monthly evolution of clients...");
        Map<String, Long> monthlyCounts = new HashMap<>();

        LocalDate startDate = LocalDate.now().minusYears(1);
        YearMonth currentMonth = YearMonth.from(startDate);
        ZoneId zone = ZoneId.systemDefault();

        for (int i = 0; i < 12; i++) {
            YearMonth month = currentMonth.plusMonths(i);

            Instant startInstant = month.atDay(1).atStartOfDay(zone).toInstant();
            Instant endInstant = month.atEndOfMonth().atTime(23, 59, 59).atZone(zone).toInstant();

            long count = userRepository.countClientsByCreatedAtBetween(startInstant, endInstant);

            monthlyCounts.put(month.format(DateTimeFormatter.ofPattern("yyyy-MM")), count);
        }

        log.info("✅ Monthly client evolution calculated: {}", monthlyCounts);
        return monthlyCounts;
    }


}