package ordering.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Service
@Slf4j
public class FCMService {

    private boolean firebaseInitialized = false;

    @PostConstruct
    public void initialize() {
        try {
            ClassPathResource resource = new ClassPathResource("firebase/firebase-adminsdk.json");
            if (!resource.exists()) {
                log.warn("⚠️ Firebase service account file [firebase-adminsdk.json] not found. FCM notifications will be disabled.");
                return;
            }
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                firebaseInitialized = true;
                log.info("✅ Firebase application initialized");
            }
        } catch (IOException e) {
            log.error("❌ Failed to initialize Firebase: {}", e.getMessage(), e);
            firebaseInitialized = false;
        }
    }

    public void sendToModerator(String token, String title, String contents) {
        if (!firebaseInitialized) {
            log.warn("⚠️ Firebase not initialized. Skipping notification to token: {}", token);
            return;
        }

        log.info("FCMService sendMessage - token: {}, title: {}, contents: {}", token, title, contents);

        if (token == null || token.trim().isEmpty()) {
            log.warn("⚠️ FCM token is null or empty, skipping notification");
            return;
        }

        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder()
                        .putHeader("Urgency", "high")
                        .setNotification(WebpushNotification.builder()
                                .setTitle(title)
                                .setBody(contents)
                                .build())
                        .build())
                .build();

        try {
            String messageResponse = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM message sent successfully: {}", messageResponse);
        } catch (FirebaseMessagingException e) {
            log.error("❌ Failed to send FCM message: {}", e.getMessage(), e);
        }
    }
    public void sendToClient(String fcmToken, String title, String body) {
        if (!firebaseInitialized) {
            log.warn("⚠️ Firebase not initialized. Skipping notification to token: {}", fcmToken);
            return;
        }

        log.info("FCMService sendMessage - token: {}, title: {}, contents: {}", fcmToken, title, body);

        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.warn("⚠️ FCM token is null or empty, skipping notification");
            return;
        }
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(fcmToken)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info(" ✅ Successfully sent notification: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error(" ❌ Failed to send notification to token {}: {}", fcmToken, e.getMessage(), e);
        }
    }


}