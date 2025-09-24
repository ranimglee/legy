package user.infrastructure.persistence;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import user.infrastructure.persistence.entities.MongoVerificationToken;

import java.util.Optional;

@Repository
public interface MongoVerificationTokenRepository extends MongoRepository<MongoVerificationToken, String> {
    Optional<MongoVerificationToken> findByToken(String token);
}
