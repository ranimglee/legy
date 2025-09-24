
package user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.application.dto.in.LivreurDTO;
import user.domain.model.Status;
import user.domain.model.UserEntity;
import user.domain.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    
    public Optional<UserEntity> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserEntity> findAllNonModerateurUsers() {
        return userRepository.findAllNonModerateurUsers();
    }

    public Map<String, Long> getConnectedUserSummary() {
        return userRepository.countConnectedUsersByRole();
    }
    public long getTotalActiveUsers() {
        return userRepository.countActiveUsers();
    }

    public Optional<UserEntity> getUserById(String userId) {
        return userRepository.findById(userId);
    }
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }
    public void banUserById(String userId) {
        userRepository.updateStatus(userId, Status.BANNED);
    }

    public LivreurDTO getLivreurDetails(String livreurId) {
        Optional<UserEntity> optionalUser = userRepository.findById(livreurId);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Livreur not found with ID: " + livreurId);
        }

        UserEntity user = optionalUser.get();

        return new LivreurDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }


}