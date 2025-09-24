package payment.infrastructure.persistence.notification;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import payment.domain.model.Notification;
import payment.domain.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Repository

public class NotificationAdapter implements NotificationRepository {
    private final NotificationMongoRepository mongoRepository;

    public NotificationAdapter(NotificationMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationDocument document = NotificationDocument.fromDomain(notification);
        return mongoRepository.save(document).toDomain();
    }

    @Override
    public List<Notification> findAll() {
        return mongoRepository.findAll().stream()
                .map(NotificationDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Notification> findById(String id) {
        return mongoRepository.findById(id).map(NotificationDocument::toDomain);
    }

    @Override
    public void deleteAll(List<Notification> oldReadNotifications) {
        List<NotificationDocument> documents = oldReadNotifications.stream()
                .map(NotificationDocument::fromDomain)
                .collect(Collectors.toList());
        mongoRepository.deleteAll(documents);
    }

    @Override
    public List<Notification> findReadNotificationsBefore(LocalDateTime thresholdDate) {
        return mongoRepository.findByReadIsTrueAndSentAtBefore(thresholdDate).stream()
                .map(NotificationDocument::toDomain)
                .collect(Collectors.toList());
    }


}
