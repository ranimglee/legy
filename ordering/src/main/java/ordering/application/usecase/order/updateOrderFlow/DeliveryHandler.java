package ordering.application.usecase.order.updateOrderFlow;

import lombok.extern.slf4j.Slf4j;
import ordering.domain.event.OrderDeliveredEvent;
import ordering.domain.model.Order;
import ordering.infrastructure.kafka.DeliveredOrderProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DeliveryHandler {

    private final DeliveredOrderProducer deliveredOrderProducer;
    private final SimpMessagingTemplate messagingTemplate;
    @Qualifier("countRedisTemplate")
    private final StringRedisTemplate redisTemplate;
    private final OrderNotificationService notificationService;

    public DeliveryHandler(DeliveredOrderProducer deliveredOrderProducer, SimpMessagingTemplate messagingTemplate, @Qualifier("countRedisTemplate") StringRedisTemplate redisTemplate, OrderNotificationService notificationService) {
        this.deliveredOrderProducer = deliveredOrderProducer;
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.notificationService = notificationService;
    }

    public void handleDelivery(Order order, double km, int etaMinutes) {
        final String orderId = order.getId();
        log.info("📦 Order {} marked as DELIVERED, sending Kafka event...", orderId);

        final Date deliveredAt = new Date();
        final Date acceptedAt = order.getDeliveryInfo().getAcceptedAt();

        long deliveryDurationMinutes = -1;
        if (acceptedAt != null) {
            deliveryDurationMinutes = TimeUnit.MILLISECONDS.toMinutes(deliveredAt.getTime() - acceptedAt.getTime());
            log.info("⏱️ Delivery Time for Order {} by Livreur {}: {} minutes",
                    orderId, order.getDeliveryInfo().getDeliveryPersonId(), deliveryDurationMinutes);
        } else {
            log.warn("⚠️ AcceptedAt is null — cannot compute delivery time for Order {}", orderId);
        }

        boolean deliveredOnTime = false;
        if (etaMinutes > 0 && deliveryDurationMinutes >= 0) {
            deliveredOnTime = deliveryDurationMinutes <= etaMinutes;
            if (deliveredOnTime) {
                log.info("🚀 Order {} was delivered ON TIME! ETA: {} min, Actual: {} min", orderId, etaMinutes, deliveryDurationMinutes);
            } else {
                log.warn("🐌 Order {} was delivered LATE! ETA: {} min, Actual: {} min", orderId, etaMinutes, deliveryDurationMinutes);

                final String livreurId = order.getDeliveryInfo().getDeliveryPersonId();
                final String alertMsg = String.format(
                        "🐌 Order %s was delivered LATE. ETA was %d min, but it took %d min.",
                        orderId, etaMinutes, deliveryDurationMinutes
                );

                messagingTemplate.convertAndSend("/topic/livreur/" + livreurId + "/late-alert", alertMsg);
                notificationService.saveNotificationInRedis(
                        "livreur", livreurId, orderId,
                        String.format("La commande %s a été livrée en retard. ETA était %d min, mais cela a pris %d min.",
                                orderId, etaMinutes, deliveryDurationMinutes)
                );
                log.warn("⚠️ Late delivery alert sent to Livreur {} for Order {}", livreurId, orderId);
            }
        }

        final Long assignmentCount = getAssignmentCount(order.getDeliveryInfo().getDeliveryPersonId());

        log.info("📦 Order DELIVERED — sending event with distanceKm: {}", km);

        OrderDeliveredEvent event = OrderDeliveredEvent.builder()
                .orderId(orderId)
                .total(order.getTotal())
                .client(order.getClient())
                .restaurant(order.getRestaurant())
                .deliveryInfo(order.getDeliveryInfo())
                .items(order.getItems())
                .deliveredAt(deliveredAt)
                .distanceKm(km)
                .deliveryDurationMinutes((double) deliveryDurationMinutes)
                .deliveredOnTime(deliveredOnTime)
                .assignmentCount(assignmentCount)
                .build();

        deliveredOrderProducer.sendDeliveredEvent(event);

        notifyRestaurant(orderId, order.getRestaurant().getRestaurantId());

        updateLivreurToFree(order.getDeliveryInfo().getDeliveryPersonId(), orderId);
    }

    private Long getAssignmentCount(String livreurId) {
        try {
            String assignmentKey = "livreur:stats:" + livreurId;
            Object countObj = redisTemplate.opsForHash().get(assignmentKey, "assignments");
            if (countObj != null) {
                Long count = Long.parseLong(countObj.toString());
                log.info("📦 Livreur {} has been assigned {} times (from Redis)", livreurId, count);
                return count;
            }
        } catch (Exception e) {
            log.warn("⚠️ Failed to fetch assignment count for livreur {}: {}", livreurId, e.getMessage());
        }
        return null;
    }

    private void notifyRestaurant(String orderId, String restaurantId) {
        Map<String, String> restaurantPayload = Map.of(
                "orderId", orderId,
                "message", "✅ La commande a été livrée avec succès au client."
        );

        messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId + "/order-status", restaurantPayload);
        notificationService.saveNotificationInRedis("restaurant", restaurantId, orderId, "La commande a été livrée avec succès au client");
        log.info("🔔 Notification de livraison envoyée au restaurant {} pour la commande {}", restaurantId, orderId);
    }

    private void updateLivreurToFree(String livreurId, String orderId) {
        try {
            redisTemplate.opsForValue().set("livreur:" + livreurId + ":status", "FREE");
            log.info("🔄 Livreur {} status set back to FREE after delivering order {}", livreurId, orderId);
        } catch (Exception e) {
            log.error("❌ Failed to update livreur status to FREE in Redis: {}", e.getMessage(), e);
        }
    }
}
