package restaurant.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import restaurant.infrastructure.Document.MongoProductFavorite;

import java.util.Optional;

interface SpringDataProductFavoriteRepository
        extends MongoRepository<MongoProductFavorite, String> {

    Optional<MongoProductFavorite> findByUserIdAndProductId(String userId, String productId);

    void deleteByUserIdAndProductId(String userId, String productId);

    Page<MongoProductFavorite> findByUserId(String userId, Pageable pageable);
}
