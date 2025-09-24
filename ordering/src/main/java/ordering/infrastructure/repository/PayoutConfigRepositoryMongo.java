package ordering.infrastructure.repository;

import ordering.infrastructure.Document.PayoutConfigDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PayoutConfigRepositoryMongo extends MongoRepository<PayoutConfigDocument, String> {
}