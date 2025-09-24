package ordering.notification;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.domain.model.UserToken;
import ordering.domain.repository.UserTokenRepository;
import ordering.infrastructure.Document.UserTokenDocument;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserTokenService {
    private final  UserTokenRepository userTokenRepository;

    public void saveToken(String userId, String fcmToken) {
        log.debug("Received FCM token for userId: {}, raw token: {}", userId, fcmToken);
        String cleanedToken = cleanToken(fcmToken);
        log.debug("Cleaned FCM token for userId: {}, token: {}", userId, cleanedToken);

        UserToken userToken = new UserToken();
        userToken.setUserId(userId);
        userToken.setFcmToken(cleanedToken);
        userToken.setCreatedAt(Instant.now());
// Check for existing token to handle upsert
        Optional<UserToken> existingDocument = userTokenRepository.findByUserId(userId);
        existingDocument.ifPresent(doc -> {
            log.debug("Existing token document found for userId: {}, id: {}", userId, doc.getId());
            userToken.setId(doc.getId()); // Set ID for update instead of insert
        });
        UserToken saved = userTokenRepository.saveToken(userToken);
        log.info("FCM token saved for userId: {}, stored token: {}", userId, saved.getFcmToken());
    }

    private String cleanToken(String fcmToken) {
        try {
            if (fcmToken != null && fcmToken.startsWith("{\"token\":\"") && fcmToken.endsWith("\"}")) {
                log.warn("Cleaning JSON-wrapped FCM token: {}", fcmToken);
                return fcmToken.substring(10, fcmToken.length() - 2); // Extract "token" value
            }
            return fcmToken != null ? fcmToken : "";
        } catch (Exception e) {
            log.error("Failed to clean FCM token: {}, error: {}", fcmToken, e.getMessage(), e);
            return fcmToken != null ? fcmToken : "";
        }
    }
}
