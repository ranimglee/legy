package shared.domain;


import java.time.Instant;

/**
 * Immutable DTO for pushing order-status notifications.
 */
public record OrderNotification(
        String notificationId,
        String orderId,
        String userId,
        String status,
        String message,
        Instant timestamp
) {
}

