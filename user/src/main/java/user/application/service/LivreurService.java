package user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user.application.dto.in.LivreurResponse;
import user.application.dto.out.ChangePasswordRequest;
import user.application.dto.out.RegisterLivreurRequest;
import user.application.exception.EmailAlreadyExistsException;
import user.domain.model.FinancierEntity;
import user.domain.model.LivreurEntity;
import user.domain.repository.UserRepository;
import user.infrastructure.persistence.UserRepositoryImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivreurService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final DeliveryZoneService deliveryZoneService;


    public LivreurResponse registerLivreur(RegisterLivreurRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        String rawPassword = generateSecurePassword();

        LivreurEntity livreur = new LivreurEntity();
        livreur.setUsername(request.username());
        livreur.setFirstname(request.firstname());
        livreur.setLastname(request.lastname());
        livreur.setEmail(request.email());
        livreur.setPassword(passwordEncoder.encode(rawPassword));
        livreur.setPhoneNumber(request.phoneNumber());
        livreur.setAddress(request.address());
        livreur.setRib(request.rib());
        livreur.setMatricule(request.matricule());
        livreur.setLatitude(request.latitude());
        livreur.setLongitude(request.longitude());
        livreur.setIsAssigned(true);
        livreur = userRepository.saveLivreur(livreur);
        try {
            deliveryZoneService.assignDriverToZone(livreur.getId(), request.zoneId());
            livreur.setIsAssigned(true);
            log.info("Livreur {} successfully assigned to zone {}", livreur.getId(), request.zoneId());
        } catch (Exception e) {
            log.error("Failed to assign livreur {} to zone {}: {}", livreur.getId(), request.zoneId(), e.getMessage());
            livreur.setIsAssigned(false);
        }



        emailService.sendWelcomeEmail(livreur.getEmail(), livreur.getUsername(), rawPassword, "LIVREUR");

        return new LivreurResponse(
                livreur.getId(),
                livreur.getUsername(),
                livreur.getFirstname(),
                livreur.getLastname(),
                livreur.getEmail(),
                livreur.getPhoneNumber(),
                livreur.getAddress(),
                livreur.getRib(),
                livreur.getMatricule(),
                livreur.getIsAssigned(),
                livreur.getStatus(),
                livreur.getRole()
        );
    }


    private String generateSecurePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12); // 12-char random password
    }
    public Map<String, Long> getMonthlyLivreurEvolution() {
        log.info("📊 Calculating monthly evolution of clients...");
        Map<String, Long> monthlyCounts = new HashMap<>();

        LocalDate startDate = LocalDate.now().minusYears(1); // 12 months ago
        YearMonth currentMonth = YearMonth.from(startDate);
        ZoneId zone = ZoneId.systemDefault(); // ou ZoneId.of("UTC") selon ton besoin

        for (int i = 0; i < 12; i++) {
            YearMonth month = currentMonth.plusMonths(i);

            Instant startInstant = month.atDay(1).atStartOfDay(zone).toInstant();
            Instant endInstant = month.atEndOfMonth().atTime(23, 59, 59).atZone(zone).toInstant();

            long count = userRepository.countDriversByCreatedAtBetween(startInstant, endInstant);

            monthlyCounts.put(month.format(DateTimeFormatter.ofPattern("yyyy-MM")), count);
        }

        log.info("✅ Monthly client evolution calculated: {}", monthlyCounts);
        return monthlyCounts;
    }


}