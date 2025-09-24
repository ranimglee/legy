package ordering.adapters.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.infrastructure.NotificationDocument;
import shared.infrastructure.NotificationRepository;

import java.time.Instant;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class OrderNotificationController {

    private final NotificationRepository notificationRepository;

    public record NotificationResponse(
            String id,
            String orderId,
            String userId,
            String trackingStatus,
            String message,
            Instant timestamp
    ) {
    }

    /**
     * GET /api/v1/notifications?orderId=...&userId=...
     * Either orderId or userId must be provided.
     */
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (orderId == null && userId == null) {
            return ResponseEntity.badRequest().build();
        }

        Page<NotificationDocument> docs = (userId != null)
                ? notificationRepository.findByUserIdOrderByTimestampDesc(userId, PageRequest.of(page, size))
                : notificationRepository.findByOrderIdOrderByTimestampDesc(orderId, PageRequest.of(page, size));

        Page<NotificationResponse> result = docs.map(doc -> new NotificationResponse(
                doc.getId(),
                doc.getOrderId(),
                doc.getUserId(),
                doc.getTrackingStatus(),
                doc.getMessage(),
                doc.getTimestamp()
        ));

        return ResponseEntity.ok(result);
    }
}
