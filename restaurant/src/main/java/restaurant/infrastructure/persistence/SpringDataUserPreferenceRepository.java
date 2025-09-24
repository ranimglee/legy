package restaurant.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import restaurant.infrastructure.Document.MongoUserPreference;

import java.util.Optional;

public interface SpringDataUserPreferenceRepository
        extends MongoRepository<MongoUserPreference, String> {

    Optional<MongoUserPreference> findByUserId(String userId);

    void deleteByUserId(String userId);
}
