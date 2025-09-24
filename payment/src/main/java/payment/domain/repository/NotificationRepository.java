package payment.domain.repository;

import payment.domain.model.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification); // Return the saved notification

    List<Notification> findAll();

    Optional<Notification> findById(String id);



    void deleteAll(List<Notification> oldReadNotifications);

    List<Notification> findReadNotificationsBefore(LocalDateTime thresholdDate);
}
