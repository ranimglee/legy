package user.application.service.notification;

import org.springframework.stereotype.Component;

interface NotificationService {
    void sendPushNotification(String deviceToken,
                              String title, String body, String deepLink) throws Exception;
}
