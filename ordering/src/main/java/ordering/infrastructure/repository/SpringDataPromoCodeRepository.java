package ordering.infrastructure.repository;

import ordering.infrastructure.Document.PromoCodeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpringDataPromoCodeRepository extends MongoRepository<PromoCodeDocument, String> {
    Optional<PromoCodeDocument> findByCode(String code);
}
