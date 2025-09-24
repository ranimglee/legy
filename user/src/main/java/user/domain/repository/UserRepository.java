package user.domain.repository;


import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.catalina.User;
import org.springframework.data.domain.PageRequest;

import user.domain.model.*;

import java.awt.print.Pageable;
import java.sql.Driver;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface UserRepository {

    Optional<UserEntity> findByEmail(String email);

    UserEntity saveUser(UserEntity user);

    ClientEntity saveClient(ClientEntity client);

    Optional<UserEntity> findById(String userId);

    Optional<ClientEntity> findClientById(String userId);
    LivreurEntity saveLivreur(LivreurEntity livreur);

    RestaurantManagerEntity saveRestaurantManager(RestaurantManagerEntity manager);

    ModerateurEntity saveModerateur(ModerateurEntity moderateur);
    FinancierEntity saveFinancier(FinancierEntity financier);


    List<LivreurEntity> findAvailableLivreurs();

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByUsername(String username);
    List<ClientEntity> findByIdIn(List<String> ids);


    List<UserEntity> findAllNonModerateurUsers();
     Map<String, Long> countConnectedUsersByRole();
     long countActiveUsers() ;
     void deleteById(String id);
     void updateStatus(String userId, Status status);

    List<LivreurEntity> findByIsAssignedFalse();


    List<LivreurEntity> findByIdInAndStatus(List<String> livreurIds, Status status);

    void updateFcmToken(String userId, String fcmToken);

    List<LivreurEntity> findAllLivreurs();

    long countClientsByCreatedAtBetween(Instant localDateTime, Instant localDateTime1);

    long countDriversByCreatedAtBetween(Instant startInstant, Instant endInstant);
}

 




