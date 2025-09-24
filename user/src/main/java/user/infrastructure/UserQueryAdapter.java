package user.infrastructure;


import org.springframework.stereotype.Component;
import shared.dto.UserInfo;
import shared.port.UserQueryPort;
import user.domain.model.UserEntity;
import user.domain.repository.UserRepository;

@Component
public class UserQueryAdapter implements UserQueryPort {

    private final UserRepository userRepository;

    public UserQueryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserInfo getUserByUsername(String username) {
        UserEntity u = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return toDto(u);
    }

    @Override
    public UserInfo getUserById(String userId) {
        UserEntity u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return toDto(u);
    }

    private UserInfo toDto(UserEntity u) {
        return new UserInfo(
                u.getId(),
                u.getUsername(),
                u.getFirstname(),
                u.getLastname(),
                u.getEmail()
        );
    }
}
