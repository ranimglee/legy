package ordering.application.usecase.order.updateOrderFlow;

import lombok.extern.slf4j.Slf4j;
import ordering.domain.model.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class OrderNotificationService {
    private static final String ORDER_ID = "orderId";
    private static final String MESSAGE = "message";
    private static final String ORDER_STATUS_TOPIC = "/order-status";
    private static final String CLIENT_TOPIC_PREFIX = "/topic/client/";
    private static final String LIVREUR_TOPIC_PREFIX = "/topic/livreur/";
    private static final String CLIENT_SCOPE = "client";

    private final SimpMessagingTemplate messagingTemplate;
    @Qualifier("countRedisTemplate")
    private final StringRedisTemplate redisTemplate;

    public OrderNotificationService(SimpMessagingTemplate messagingTemplate,    @Qualifier("countRedisTemplate")
    StringRedisTemplate redisTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
    }

    public void notifyClientOrderAccepted(Order order) {
        log.info("✅ Order {} status set to ACCEPTED, sending notification to client...", order.getId());
        String clientId = order.getClient().getClientId();
        Map<String, String> clientPayload = Map.of(
                ORDER_ID, order.getId(),
                MESSAGE, "Your order has been accepted and is being prepared!"
        );
        messagingTemplate.convertAndSend(
                CLIENT_TOPIC_PREFIX + clientId + ORDER_STATUS_TOPIC,
                clientPayload);
        saveNotificationInRedis(CLIENT_SCOPE, clientId, order.getId(), "Votre commande a été acceptée et est en preparation");
        log.info("🔔 Sent order-accepted notification to Client {} for Order {}", clientId, order.getId());
    }

    public void notifyClientOrderRefused(Order order) {
        log.info("✅ Order {} status set to REFUSED, sending notification to client...", order.getId());
        String clientId = order.getClient().getClientId();
        Map<String, String> clientPayload = Map.of(
                ORDER_ID, order.getId(),
                MESSAGE, "Your order has been refused we will contact you soon"
        );
        messagingTemplate.convertAndSend(
                CLIENT_TOPIC_PREFIX + clientId + ORDER_STATUS_TOPIC,
                clientPayload
        );
        saveNotificationInRedis(CLIENT_SCOPE, clientId, order.getId(), "Votre commande a été refusée, nous vous contacterons bientôt");
        log.info("🔔 Sent order-refused notification to Client {} for Order {}", clientId, order.getId());
    }

    public void notifyLivreurStartTracking(Order order) {
        String livreurId = order.getDeliveryInfo().getDeliveryPersonId();
        Map<String, String> livreurPayload = Map.of(
                ORDER_ID, order.getId(),
                "intervalSec", "2"
        );
        messagingTemplate.convertAndSend(
                LIVREUR_TOPIC_PREFIX + livreurId + "/start-tracking",
                livreurPayload
        );
        saveNotificationInRedis("livreur", livreurId, order.getId(), "Démarrer le suivi pour la commande " + order.getId() + " avec un intervalle de 2 secondes");
        log.info("▶️ Sent start-tracking to Livreur {} for Order {}", livreurId, order.getId());
    }

    public void notifyClientOrderPickedUp(Order order) {
        String clientId = order.getClient().getClientId();
        Map<String, String> clientPayload = Map.of(
                ORDER_ID, order.getId(),
                MESSAGE, "Your order has been picked up! You can now track your delivery driver."
        );
        messagingTemplate.convertAndSend(
                CLIENT_TOPIC_PREFIX + clientId + ORDER_STATUS_TOPIC,
                clientPayload
        );
        saveNotificationInRedis(CLIENT_SCOPE, clientId, order.getId(), "Votre commande a été récupérée ! Vous pouvez maintenant suivre votre livreur");
        log.info("🔔 Sent picked-up notification to Client {} for Order {}", clientId, order.getId());
    }

    public void saveNotificationInRedis(String scope, String targetId, String orderId, String message) {
        String redisKey = String.format("notif:%s:%s:order:%s", scope, targetId, orderId);
        redisTemplate.opsForValue().set(redisKey, message, Duration.ofHours(24));
    }
}

