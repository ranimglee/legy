package user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user.application.dto.in.ClientProfileResponse;
import user.application.dto.in.ModerateurResponse;
import user.application.dto.in.UpdateModeratorProfileRequest;
import user.application.dto.out.ModeratorProfileResponse;
import user.application.dto.out.RegisterModerateurRequest;
import user.application.dto.out.UpdateClientProfileRequest;
import user.application.exception.EmailAlreadyExistsException;
import user.application.exception.UserNotFoundException;
import user.domain.model.ClientEntity;
import user.domain.model.ModerateurEntity;
import user.domain.repository.UserRepository;
import user.infrastructure.persistence.UserRepositoryImpl;

@Service
@RequiredArgsConstructor
public class ModerateurService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public ModerateurResponse registerModerateur(RegisterModerateurRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        String rawPassword = request.password();
        ModerateurEntity moderateur = new ModerateurEntity();
        moderateur.setUsername(request.username());
        moderateur.setFirstname(request.firstname());
        moderateur.setLastname(request.lastname());
        moderateur.setEmail(request.email());
        moderateur.setPassword(passwordEncoder.encode(request.password()));
        moderateur.setPhoneNumber(request.phoneNumber());
        moderateur.setRib(request.rib());
        moderateur.setCreatedAt(request.createdAt());


        moderateur =  userRepository.saveModerateur(moderateur);
        emailService.sendWelcomeEmail(moderateur.getEmail(), moderateur.getUsername(), rawPassword, "MODERATEUR");
        return new ModerateurResponse(
                moderateur.getUsername(),
                moderateur.getFirstname(),
                moderateur.getLastname(),
                moderateur.getEmail(),
                moderateur.getPhoneNumber(),
                moderateur.getRib(),
                moderateur.getCreatedAt()
        );
    }


    public ModeratorProfileResponse updateProfile(String email, UpdateModeratorProfileRequest request) {
        ModerateurEntity moderateur = (ModerateurEntity) userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Moderator not found"));

        moderateur.setFirstname(request.firstName());
        moderateur.setLastname(request.lastName());
        moderateur.setUsername(request.username());
        moderateur.setPhoneNumber(request.phoneNumber());
        moderateur.setRib(request.rib());

        userRepository.saveUser(moderateur);

        return ModeratorProfileResponse.builder()
                .id(moderateur.getId())
                .username(moderateur.getUsername())
                .firstname(moderateur.getFirstname())
                .lastname(moderateur.getLastname())
                .email(moderateur.getEmail())
                .phoneNumber(moderateur.getPhoneNumber())
                .rib(moderateur.getRib())
                .createdAt(moderateur.getCreatedAt())
                .build();
    }
}