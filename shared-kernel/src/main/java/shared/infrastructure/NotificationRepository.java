package shared.infrastructure;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface NotificationRepository
        extends MongoRepository<NotificationDocument, String> {
    Page<NotificationDocument> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    Page<NotificationDocument> findByOrderIdOrderByTimestampDesc(String orderId, Pageable pageable);
}
