package payment.infrastructure.persistence.notification;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import payment.domain.model.Notification;

import java.time.LocalDateTime;
import java.util.List;
@Repository

public interface NotificationMongoRepository extends MongoRepository<NotificationDocument, String> {
    @Query("{'read': true, 'sentAt': { $lt: ?0 } }")
    List<NotificationDocument> findByReadIsTrueAndSentAtBefore(@Param("thresholdDate") LocalDateTime thresholdDate);
}