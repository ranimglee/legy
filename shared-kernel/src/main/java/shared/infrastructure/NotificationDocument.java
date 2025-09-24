package shared.infrastructure;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import shared.domain.OrderNotification;

import java.time.Instant;

@Document("notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDocument {
    @Id
    private String id;
    private String orderId;
    private String userId;
    private String trackingStatus;
    private String message;
    private Instant timestamp;

    public static NotificationDocument from(OrderNotification n) {
        return NotificationDocument.builder()
                .id(n.notificationId())
                .orderId(n.orderId())
                .userId(n.userId())
                .trackingStatus(n.status())
                .message(n.message())
                .timestamp(n.timestamp())
                .build();
    }
}

