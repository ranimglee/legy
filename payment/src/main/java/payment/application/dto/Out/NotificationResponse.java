package payment.application.dto.Out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import payment.domain.model.Notification;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private String message;
    private List<Notification> notifications;
    private String error;

    public NotificationResponse(String message, List<Notification> notifications) {
        this.message = message;
        this.notifications = notifications;
    }

    public NotificationResponse(String error) {
        this.error = error;
    }
}
