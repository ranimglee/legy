package user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user.application.dto.in.FinancierResponse;
import user.application.dto.in.UpdateFinancialProfileRequest;
import user.application.dto.out.FinancialProfileResponse;
import user.application.dto.out.RegisterFinancierRequest;
import user.application.exception.EmailAlreadyExistsException;
import user.application.exception.UserNotFoundException;
import user.domain.model.FinancierEntity;
import user.domain.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancierService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public FinancierResponse registerFinancier(RegisterFinancierRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        String rawPassword = generateSecurePassword();

        FinancierEntity financier = new FinancierEntity();
        financier.setUsername(request.username());
        financier.setFirstname(request.firstname());
        financier.setLastname(request.lastname());
        financier.setEmail(request.email());
        financier.setPassword(passwordEncoder.encode(rawPassword));
        financier.setPhoneNumber(request.phoneNumber());
        financier.setRib(request.rib());
        financier.setLatitude(request.latitude());
        financier.setLongitude(request.longitude());


        financier = userRepository.saveFinancier(financier);



        emailService.sendWelcomeEmail(financier.getEmail(), financier.getUsername(), rawPassword, "financier");

        return new FinancierResponse(
                financier.getId(),
                financier.getUsername(),
                financier.getFirstname(),
                financier.getLastname(),
                financier.getEmail(),
                financier.getPhoneNumber(),
                financier.getRib(),
                financier.getLongitude(),
                financier.getLatitude(),
                financier.getRole()
        );
    }


    private String generateSecurePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12); // 12-char random password
    }


    public FinancialProfileResponse updateProfile(String email, UpdateFinancialProfileRequest request) {
        FinancierEntity financier = (FinancierEntity) userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Financier not found"));

        financier.setFirstname(request.firstname());
        financier.setLastname(request.lastname());
        financier.setUsername(request.username());
        financier.setPhoneNumber(request.phoneNumber());
        financier.setRib(request.rib());


        userRepository.saveUser(financier);

        return FinancialProfileResponse.builder()
                .id(financier.getId())
                .username(financier.getUsername())
                .firstname(financier.getFirstname())
                .lastname(financier.getLastname())
                .email(financier.getEmail())
                .phoneNumber(financier.getPhoneNumber())
                .rib(financier.getRib())
                .build();
    }


}


