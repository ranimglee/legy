package user.application.service.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationService.class);

    @Override
    public void sendPushNotification(String deviceToken, String title, String body, String deepLink) throws Exception {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("deepLink", deepLink)
                .setToken(deviceToken)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent notification to {}: {}", deviceToken, response);
        } catch (Exception e) {
            log.error("Failed to send notification to {}: {}", deviceToken, e.getMessage());
            throw e;
        }
    }
}