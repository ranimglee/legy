package ordering.notification;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.orderIssue.ModeratorNotificationDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ordering.application.dto.orderIssue.OrderIssueNotification;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisNotificationService {

    private final RedisTemplate<String, OrderIssueNotification> redisTemplate;
    private final RedisTemplate<String, ModeratorNotificationDTO> redisModeratorTemplate;

    private static final String NOTIFICATION_KEY_PREFIX = "moderator:notifications:";
    private static final String MODERATOR_NOTIFICATION_LIST_KEY = "moderator:notifications:recent";
    private static final Duration TTL = Duration.ofDays(1);
    public void saveNotification(OrderIssueNotification notification) {
        String key = NOTIFICATION_KEY_PREFIX + notification.issueRef();
        redisTemplate.opsForValue().set(key, notification, Duration.ofHours(12)); // store for 12 hours
    }


    public void saveModeratorNotification(ModeratorNotificationDTO notification) {
        redisModeratorTemplate.opsForList().leftPush(MODERATOR_NOTIFICATION_LIST_KEY, notification);
        redisModeratorTemplate.expire(MODERATOR_NOTIFICATION_LIST_KEY, TTL);
    }
}
