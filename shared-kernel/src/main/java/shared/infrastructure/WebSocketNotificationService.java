package shared.infrastructure;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import shared.domain.OrderNotification;
import shared.domain.service.NotificationService;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService implements NotificationService {

    private final NotificationRepository repo;
    private final SimpMessagingTemplate ws;

    @Override
    public void send(OrderNotification notification) {
        // 1) Persist
        repo.save(NotificationDocument.from(notification));

        // 2) Push over WebSocket STOMP to /topic/notifications/{userId}
        ws.convertAndSend(
                "/topic/notifications/" + notification.userId(),
                notification
        );
    }


}
