package ordering.infrastructure.repository;

import io.micrometer.observation.ObservationFilter;
import ordering.infrastructure.Document.UserTokenDocument;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoUserTokenRepository extends MongoRepository<UserTokenDocument, String> {
    Optional<UserTokenDocument> findByUserId(String userId);

    Optional<UserTokenDocument> findByFcmToken(String cleanedToken);
}
