package payment.adapters.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payment.application.dto.Out.NotificationResponse;
import payment.application.usecase.NotificationUseCase;
import payment.domain.model.Notification;
import payment.domain.model.NotificationSetting;
import payment.domain.repository.NotificationSettingRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/financier/notifications")
public class NotificationController {

    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationUseCase notificationUseCase;

    public NotificationController(NotificationSettingRepository notificationSettingRepository, NotificationUseCase notificationUseCase) {
        this.notificationSettingRepository = notificationSettingRepository;
        this.notificationUseCase = notificationUseCase;
    }


    /**
     * Updates the notification delay to the specified number of days.
     * If no notification setting exists yet, a new one is created.
     *
     * @param days the number of days to notify before a payment is due
     * @return a response containing a success message
     */

    @PostMapping("/update-notification-settings")
    public ResponseEntity<NotificationResponse> updateNotificationDelay(@RequestParam int days) {
        try {
            NotificationSetting setting = notificationSettingRepository.findAll().stream().findFirst()
                    .orElse(new NotificationSetting());
            setting.setNotificationDelayInDays(days);
            notificationSettingRepository.save(setting);
            return ResponseEntity.status(HttpStatus.CREATED).body(new NotificationResponse("Notification delay updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse("Error updating notification delay: " + e.getMessage()));
        }
    }

    /**
     * Triggers the notification process for upcoming payments.
     *
     * This method initiates the sending of notifications for payments that are due
     * based on the notification settings. It returns a response entity indicating
     * the success or failure of the operation. If the notifications are triggered
     * successfully, a response with HTTP status OK is returned. In case of an
     * error during the triggering process, a response with HTTP status INTERNAL_SERVER_ERROR
     * is returned.
     *
     * @return ResponseEntity containing a NotificationResponse indicating the result
     *         of the notification triggering operation or an error message.
     */

    @PostMapping("/trigger-notification")
    public ResponseEntity<NotificationResponse> triggerPaymentNotifications() {

        try {
            notificationUseCase.triggerPaymentNotifications();
            return ResponseEntity.ok(new NotificationResponse("Payment notifications triggered successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse("Error triggering payment notifications: " + e.getMessage()));
        }
    }

    /**
     * Retrieves all notifications.
     *
     * @return A response containing a list of all notifications or a response with a NO_CONTENT status if no notifications are found.
     *         In case of an error, a response with an INTERNAL_SERVER_ERROR status is returned.
     */
    @GetMapping("/get-all-notifications")
    public ResponseEntity<NotificationResponse> getAllNotifications() {
        try {
            List<Notification> notifications = notificationUseCase.getAllNotifications();
            if (notifications.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new NotificationResponse("No notifications found", null));
            }
            return ResponseEntity.ok(new NotificationResponse("Notifications fetched successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse("Error fetching notifications: " + e.getMessage()));
        }
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId ID of the notification to mark as read.
     * @return A response with the updated notification if found, otherwise a response with a NOT_FOUND status.
     */
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(@PathVariable String notificationId) {
        try {
            Optional<Notification> updatedNotification = notificationUseCase.markNotificationAsRead(notificationId);
            return updatedNotification
                    .map(notification -> ResponseEntity.ok(new NotificationResponse("Notification marked as read", List.of(notification))))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new NotificationResponse("Notification not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse("Error marking notification as read: " + e.getMessage()));
        }
    }
}
