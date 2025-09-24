package payment.domain.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import payment.domain.model.Notification;
import payment.domain.model.NotificationSetting;
import payment.domain.model.Payment;
import payment.domain.repository.NotificationRepository;
import payment.domain.repository.NotificationSettingRepository;
import payment.domain.repository.PaymentRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;

@Service
@AllArgsConstructor
public class NotificationService {

    private final PaymentRepository paymentRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;  // Inject WebSocket messaging
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);


    @Scheduled(cron = "0 0 9 * * ?")  // every minute, adjust as needed
    public void sendPaymentNotifications() {
        try {
            int delayInDays = notificationSettingRepository.findAll().stream()
                    .findFirst()
                    .map(NotificationSetting::getNotificationDelayInDays)
                    .orElse(1);

            LocalDate notificationDate = LocalDate.now().plusDays(delayInDays);
            logger.info("Running payment notification check. NotificationDate={}", notificationDate);

            List<Payment> upcomingPayments = paymentRepository.findAll();

            if (upcomingPayments.isEmpty()) {
                logger.info("No payments found in the system.");
                return;
            }

            for (Payment payment : upcomingPayments) {
                LocalDate dueDate = payment.getDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                logger.info("Checking Payment ID={} with dueDate={}", payment.getId(), dueDate);

                if (notificationDate.isEqual(dueDate)) {
                    try {
                        Notification notification = createAndSaveNotification(payment);
                        messagingTemplate.convertAndSend("/topic/notifications", notification);
                        logger.info("✅ Notification created and sent for Payment ID={}", payment.getId());
                    } catch (Exception e) {
                        logger.error("❌ Failed to send notification for Payment ID={}. Error: {}",
                                payment.getId(), e.getMessage(), e);
                    }
                } else {
                    logger.debug("Skipping Payment ID={} (dueDate={} != notificationDate={})",
                            payment.getId(), dueDate, notificationDate);
                }
            }
        } catch (Exception e) {
            logger.error("Error sending payment notifications: {}", e.getMessage(), e);
        }
    }





    private Notification createAndSaveNotification(Payment payment) {
        try {
            String message = formatNotificationMessage(payment);
            Notification notification = new Notification(null, message, LocalDateTime.now(), false, payment);
            return notificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("Error saving notification for payment ID: " + payment.getId() + ". Error: " + e.getMessage());
            throw new RuntimeException("Error creating notification for payment ID: " + payment.getId(), e);
        }
    }

    private String formatNotificationMessage(Payment payment) {
        return String.format("Reminder: Payment of %.2f is due on %s",
                payment.getAmount(),
                payment.getDate().toString());
    }

    public List<Notification> getAllNotifications() {
        try {
            return notificationRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
            throw new RuntimeException("Error fetching notifications", e);
        }
    }

    public Optional<Notification> markNotificationAsRead(String notificationId) {
        try {
            return notificationRepository.findById(notificationId)
                    .map(notification -> {
                        notification.setRead(true);
                        Notification updatedNotification = notificationRepository.save(notification);
                        messagingTemplate.convertAndSend("/topic/notifications", updatedNotification);  // Send real-time update
                        return updatedNotification;
                    });
        } catch (Exception e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            throw new RuntimeException("Error marking notification as read", e);
        }
    }

    /**
     * Deletes notifications that have been marked as read for more than 24 hours.
     */
    @Scheduled(cron = "0 0 7 * * * ")
    public void deleteOldReadNotifications() {
        try {
            // Log the start of the method execution
            logger.info("Starting the task to delete old read notifications...");

            // Calculate the threshold time (24 hours ago), but only the date part
            LocalDateTime thresholdDate = LocalDateTime.now().minusHours(24).toLocalDate().atStartOfDay(); // Only consider the date part
            logger.info("Threshold date for deletion: {}", thresholdDate);

            // Find notifications that are read and older than the threshold date
            List<Notification> oldReadNotifications = notificationRepository.findReadNotificationsBefore(thresholdDate);

            if (oldReadNotifications.isEmpty()) {
                // Log when no notifications are found
                logger.info("No old read notifications to delete.");
            } else {
                // Log the size of the list found before deleting
                logger.info("Found {} read notifications older than 24 hours.", oldReadNotifications.size());

                // If there are any old read notifications, delete them
                notificationRepository.deleteAll(oldReadNotifications);
                logger.info("{} read notifications deleted.", oldReadNotifications.size());
            }
        } catch (Exception e) {
            // Log any errors that occur during the execution
            logger.error("Error deleting old read notifications: {}", e.getMessage(), e);
        }
    }


}
