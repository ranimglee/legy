package payment.infrastructure.persistence.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import payment.domain.model.Notification;
import payment.domain.model.Payment;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "notifications")
public class NotificationDocument {
    @Id
    private String id;
    private String message;
    private LocalDateTime sentAt;
    private boolean read;

    private Payment payment;

    public static NotificationDocument fromDomain(Notification notification) {
        return new NotificationDocument(
                notification.getId(),
                notification.getMessage(),
                notification.getSentAt(),
                notification.isRead(),
                notification.getPayment()
        );
    }

    public Notification toDomain() {
        return new Notification(
                id,
                message,
                sentAt,
                read,
                payment
        );
    }
}
