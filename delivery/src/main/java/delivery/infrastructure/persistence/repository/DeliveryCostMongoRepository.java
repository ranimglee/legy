package delivery.infrastructure.persistence.repository;

import delivery.infrastructure.persistence.document.DeliveryCostDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryCostMongoRepository extends MongoRepository<DeliveryCostDocument, String> {
    Optional<DeliveryCostDocument> findByOrderId(String orderId);
}