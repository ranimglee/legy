package ordering.application.usecase.order.updateOrderFlow;

import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.AssignOrderNotification;
import ordering.application.dto.order.ClosestLivreurRequest;
import ordering.application.dto.order.ClosestLivreurResponse;
import ordering.application.dto.order.ClosestLivreursResponse;
import ordering.domain.model.LivreurOrderStatus;
import ordering.domain.model.Order;
import ordering.domain.model.value.DeliveryInfo;
import ordering.domain.repository.OrderRepository;
import ordering.infrastructure.kafka.ClosestLivreurProducer;
import ordering.infrastructure.kafka.KafkaResponseAwaiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class LivreurAssignmentService {

    private final ClosestLivreurProducer livreurProducer;
    private final KafkaResponseAwaiter responseAwaiter;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderRepository orderRepository;
    @Qualifier("resetCodeRedisTemplate")
    private final StringRedisTemplate redisTemplate;
    private final OrderNotificationService notificationService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public LivreurAssignmentService(ClosestLivreurProducer livreurProducer, KafkaResponseAwaiter responseAwaiter, SimpMessagingTemplate messagingTemplate, OrderRepository orderRepository,    @Qualifier("resetCodeRedisTemplate")
    StringRedisTemplate redisTemplate, OrderNotificationService notificationService) {
        this.livreurProducer = livreurProducer;
        this.responseAwaiter = responseAwaiter;
        this.messagingTemplate = messagingTemplate;
        this.orderRepository = orderRepository;
        this.redisTemplate = redisTemplate;
        this.notificationService = notificationService;
    }

    public void assignLivreur(Order order, int etaMinutes) {
        String orderId = order.getId();
        String correlationId = UUID.randomUUID().toString();

        ClosestLivreurRequest request = new ClosestLivreurRequest(
                order.getRestaurant().getLatitude(),
                order.getRestaurant().getLongitude(),
                correlationId
        );

        CompletableFuture<ClosestLivreursResponse> future = new CompletableFuture<>();
        responseAwaiter.register(correlationId, future);
        livreurProducer.sendRestaurantRequest(request);
        log.info("🕒 Waiting up to 5s for livreurs response for order {} (corrId={})", orderId, correlationId);

        future.orTimeout(5, TimeUnit.SECONDS)
                .thenAccept(response -> handleLivreursResponse(order, etaMinutes, response))
                .exceptionally(ex -> {
                    log.error("🛑 Timeout or error fetching livreurs for order {}: {}", orderId, ex.getMessage());
                    notifyModeratorNoLivreur(orderId);
                    return null;
                });
    }

    private void handleLivreursResponse(Order order, int etaMinutes, ClosestLivreursResponse response) {
        String orderId = order.getId();
        List<ClosestLivreurResponse> livreurs = response.getLivreurs();

        if (livreurs.isEmpty()) {
            log.warn("🚫 No livreurs available for order {}", orderId);
            notifyModeratorNoLivreur(orderId);
            return;
        }

        log.info("📣 Received {} livreurs for order {}", livreurs.size(), orderId);

        final int[] index = {0};
        final ScheduledFuture<?>[] scheduledTask = new ScheduledFuture<?>[1];

        Runnable notifyNextLivreur = new Runnable() {
            @Override
            public void run() {
                if (index[0] >= livreurs.size()) {
                    handleNoLivreurAccepted(orderId, scheduledTask[0]);
                    return;
                }

                ClosestLivreurResponse livreur = livreurs.get(index[0]);

                AssignOrderNotification notification = new AssignOrderNotification(
                        orderId,
                        order.getRestaurant().getRestaurantId(),
                        livreur.getLivreurId(),
                        livreur.getLatitude(),
                        livreur.getLongitude(),
                        System.currentTimeMillis(),
                        order.getRestaurant().getAddress(),
                        order.getRestaurant().getPhone()
                );

                messagingTemplate.convertAndSend(
                        "/topic/livreur/" + livreur.getLivreurId(),
                        notification
                );
                notificationService.saveNotificationInRedis("livreur", livreur.getLivreurId(), orderId,
                        "Commande " + orderId + " assignée à vous");
                log.info("📤 Notification sent to Livreur {} for Order {}: [Lat={}, Lng={}]",
                        livreur.getLivreurId(), orderId, livreur.getLatitude(), livreur.getLongitude());

                order.setLivreurStatus(LivreurOrderStatus.ASSIGNED);
                orderRepository.save(order);

                incrementAssignmentCount(livreur.getLivreurId(), orderId);

                if (scheduledTask[0] != null && !scheduledTask[0].isDone()) {
                    scheduledTask[0].cancel(false);
                    log.info("🛑 Previous scheduled task cancelled for order {}", orderId);
                }

                scheduledTask[0] = scheduler.schedule(() -> {
                    Order refreshedOrder = orderRepository.findById(orderId)
                            .orElseThrow(() -> new RuntimeException("Order not found during acceptance check"));

                    LivreurOrderStatus status = refreshedOrder.getLivreurStatus();
                    if (status == LivreurOrderStatus.ACCEPTED) {
                        handleLivreurAccepted(refreshedOrder, livreur, etaMinutes, scheduledTask[0]);
                    } else if (status == LivreurOrderStatus.REFUSED) {
                        log.info("❌ Livreur {} refused order {}, moving to next livreur", livreur.getLivreurId(), orderId);
                        index[0]++;
                        scheduler.execute(this);
                    } else {
                        log.info("⏳ No acceptance after 50s for Livreur {}, marking as refused and moving to next...", livreur.getLivreurId());
                        refreshedOrder.setLivreurStatus(LivreurOrderStatus.REFUSED);
                        orderRepository.save(refreshedOrder);
                        index[0]++;
                        scheduler.execute(this);
                    }
                }, 50, TimeUnit.SECONDS);
            }
        };

        scheduledTask[0] = scheduler.schedule(notifyNextLivreur, 0, TimeUnit.SECONDS);
    }

    private void handleNoLivreurAccepted(String orderId, ScheduledFuture<?> scheduledTask) {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(false);
        }
        log.info("🚫 No livreur accepted for order {}", orderId);
        notifyModeratorNoLivreur(orderId);
    }

    private void notifyModeratorNoLivreur(String orderId) {
        Map<String, String> moderatorPayload = Map.of(
                "orderId", orderId,
                "message", "Aucun livreur n'a accepté la commande. Une action est requise."
        );
        messagingTemplate.convertAndSend("/topic/moderator/orders", moderatorPayload);
        notificationService.saveNotificationInRedis("moderator", "moderator", orderId,
                "Aucun livreur n'a accepté la commande. Une action est requise");
        log.info("🔔 Notification envoyée au modérateur pour la commande {}", orderId);
    }

    private void incrementAssignmentCount(String livreurId, String orderId) {
        try {
            String assignmentKey = "livreur:stats:" + livreurId;
            Long newCount = redisTemplate.opsForHash().increment(assignmentKey, "assignments", 1);
            log.info("📊 Livreur {} assigned to order {} — total assignments now: {}",
                    livreurId, orderId, newCount);
        } catch (Exception e) {
            log.error("❌ Failed to increment assignment count in Redis for Livreur {}: {}", livreurId, e.getMessage());
        }
    }

    private void handleLivreurAccepted(Order refreshedOrder, ClosestLivreurResponse livreur, int etaMinutes, ScheduledFuture<?> task) {
        String orderId = refreshedOrder.getId();
        log.info("✅ Order {} accepted by Livreur {} phone number {}",
                orderId, livreur.getLivreurId(), livreur.getPhoneNumber());

        redisTemplate.opsForValue().set("order:" + orderId + ":livreur", livreur.getLivreurId(), Duration.ofMinutes(60));
        log.info("📦 order:{} assigned to livreur:{}", orderId, livreur.getLivreurId());

        DeliveryInfo deliveryInfo = DeliveryInfo.builder()
                .deliveryPersonId(livreur.getLivreurId())
                .deliveryPersonName(livreur.getFirstname() + " " + livreur.getLastname())
                .contactNumber(livreur.getPhoneNumber())
                .vehicleInfo(livreur.getMatricule())
                .estimatedArrivalTime("unknown")
                .acceptedAt(new Date())
                .build();

        refreshedOrder.setDeliveryInfo(deliveryInfo);

        try {
            redisTemplate.opsForValue().set("livreur:" + deliveryInfo.getDeliveryPersonId() + ":status", "BUSY");
            log.info("🔄 Livreur {} status set to BUSY after accepting order {}", deliveryInfo.getDeliveryPersonId(), orderId);
        } catch (Exception e) {
            log.error("❌ Failed to update livreur status to BUSY in Redis: {}", e.getMessage(), e);
        }

        if (etaMinutes > 0) {
            long etaMillis = etaMinutes * 60L * 1000L;
            deliveryInfo.setEtaDeadline(new Date(deliveryInfo.getAcceptedAt().getTime() + etaMillis));
        }

        if (task != null && !task.isDone()) {
            task.cancel(false);
            log.info("🛑 Scheduled task cancelled after acceptance for Order {}", orderId);
        }

        orderRepository.save(refreshedOrder);
    }
}
