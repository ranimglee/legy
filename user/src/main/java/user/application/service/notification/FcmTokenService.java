package user.application.service.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class FcmTokenService {


    public void sendToDriver(String token, String title, String body, String deepLink) {

        if (token == null || token.trim().isEmpty()) {
            log.warn("⚠️ FCM token is null or empty, skipping notification");
            return;
        }

        try {
            WebpushNotification notification = WebpushNotification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            WebpushConfig webpushConfig = WebpushConfig.builder()
                    .setNotification(notification)
                    .putData("deepLink", deepLink) // optional but helpful
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setWebpushConfig(webpushConfig)
                    .putData("title", title)
                    .putData("body", body)
                    .putData("deepLink", deepLink) // mobile clients can use this
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM message sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("❌ Failed to send FCM message to token {}: {}", token, e.getMessage(), e);
        }
    }



}