package ordering.infrastructure.persistence;

import ordering.infrastructure.Document.ModeratorTokenDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoModeratorTokenRepository extends MongoRepository<ModeratorTokenDocument, String> {
    Optional<ModeratorTokenDocument> findByModeratorId(String moderatorId);

}