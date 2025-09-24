package payment.infrastructure.persistence.notificationSetting;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface NotificationSettingMongoRepository extends MongoRepository<NotificationSettingDocument, String> {
}
