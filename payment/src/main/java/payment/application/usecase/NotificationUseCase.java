package payment.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payment.domain.model.Notification;
import payment.domain.service.NotificationService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class NotificationUseCase {

    private final NotificationService notificationService;


    /**
     * Triggers the notification process for upcoming payments.
     */
    public void triggerPaymentNotifications() {
        notificationService.sendPaymentNotifications();
    }

    /**
     * Retrieves all notifications.
     *
     * @return List of notifications.
     */
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId ID of the notification to mark as read.
     * @return Updated notification.
     */
    public Optional<Notification> markNotificationAsRead(String notificationId) {
        return notificationService.markNotificationAsRead(notificationId);
    }

  /*  /**
     * Creates and stores a new notification.
     *
     * @param message The message for the notification.
     * @param payment The payment related to the notification.

    public void createNotification(String message, Payment payment) {
        notificationService.createNotification(message, payment);
    } */
}
