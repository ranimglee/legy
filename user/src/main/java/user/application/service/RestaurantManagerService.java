package user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shared.domain.event.RestaurantManagerCreatedEvent;
import user.application.dto.in.RestaurantManagerResponse;
import user.application.dto.out.RegisterRestaurantManagerRequest;
import user.application.exception.EmailAlreadyExistsException;
import user.domain.model.RestaurantManagerEntity;
import user.domain.repository.UserRepository;


import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantManagerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final KafkaTemplate<String, RestaurantManagerCreatedEvent> restaurantManagerKafkaTemplate;

    public RestaurantManagerResponse registerRestaurantManager(RegisterRestaurantManagerRequest request) {

        log.info("🔍 Checking if email [{}] already exists...", request.email());
        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("❌ Email already exists: {}", request.email());
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        log.info("📞 Checking if phone number [{}] already exists...", request.phoneNumber());
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            log.warn("❌ Phone number already exists: {}", request.phoneNumber());
            throw new IllegalArgumentException("Phone number already exists");
        }

        log.info("🔐 Encoding password and creating RestaurantManager entity...");
        String rawPassword = generateSecurePassword();

        RestaurantManagerEntity manager = new RestaurantManagerEntity();
        manager.setUsername(request.username());
        manager.setFirstname(request.firstname());
        manager.setLastname(request.lastname());
        manager.setEmail(request.email());
        manager.setPassword(passwordEncoder.encode(rawPassword));
        manager.setPhoneNumber(request.phoneNumber());
        manager.setRib(request.rib());
        manager.setCreatedAt(request.createdAt());

        log.info("📧 Sending welcome email to {}...", manager.getEmail());
        emailService.sendWelcomeEmail(manager.getEmail(), manager.getUsername(), rawPassword, "RESTAURANT_MANAGER");

        log.info("💾 Saving manager [{}] to the database...", manager.getEmail());
        manager = userRepository.saveRestaurantManager(manager);

        log.info("📤 Publishing Kafka event to topic [restaurant-m-created]...");
        RestaurantManagerCreatedEvent event = new RestaurantManagerCreatedEvent(
                manager.getId(),
                manager.getEmail(),
                manager.getRib(),
                manager.getUsername(),
                request.createdBy()
        );

        restaurantManagerKafkaTemplate.send("restaurant-m-created", event);

        log.info("✅ Kafka event published for manager [{}]", manager.getId());

        log.info("🎉 RestaurantManager [{}] registration completed successfully!", manager.getUsername());
        return new RestaurantManagerResponse(
                manager.getId(),
                manager.getUsername(),
                manager.getFirstname(),
                manager.getLastname(),
                manager.getEmail(),
                manager.getPhoneNumber(),
                manager.getRib(),
                manager.getRole(),
                manager.getCreatedAt()
        );
    }

    private String generateSecurePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }



}
