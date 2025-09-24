/*package ordering.application.usecase.Order;

import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.Order.*;
import ordering.application.exception.OrderNotFoundException;
import ordering.domain.event.OrderDeliveredEvent;
import ordering.domain.model.*;
import ordering.domain.model.value.DeliveryInfo;
import ordering.domain.repository.OrderRepository;
import ordering.domain.repository.RefusalHistoryRepository;
import ordering.domain.service.GoogleMapService;
import ordering.infrastructure.kafka.ClosestLivreurProducer;
import ordering.infrastructure.kafka.DeliveredOrderProducer;
import ordering.infrastructure.kafka.KafkaResponseAwaiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class UpdateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ClosestLivreurProducer livreurProducer;
    private final KafkaResponseAwaiter responseAwaiter;
    private final SimpMessagingTemplate messagingTemplate;
    private final DeliveredOrderProducer deliveredOrderProducer;
    private final GoogleMapService googleMapsService;
    @Qualifier("countRedisTemplate")
    private final StringRedisTemplate redisTemplate;
    private final RefusalHistoryRepository refusalHistoryRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public UpdateOrderUseCase(OrderRepository orderRepository, ClosestLivreurProducer livreurProducer,
                              KafkaResponseAwaiter responseAwaiter, SimpMessagingTemplate messagingTemplate,
                              DeliveredOrderProducer deliveredOrderProducer, GoogleMapService googleMapsService,
                              @Qualifier("countRedisTemplate") StringRedisTemplate redisTemplate,
                              RefusalHistoryRepository refusalHistoryRepository) {
        this.orderRepository = orderRepository;
        this.livreurProducer = livreurProducer;
        this.responseAwaiter = responseAwaiter;
        this.messagingTemplate = messagingTemplate;
        this.deliveredOrderProducer = deliveredOrderProducer;
        this.googleMapsService = googleMapsService;
        this.redisTemplate = redisTemplate;
        this.refusalHistoryRepository = refusalHistoryRepository;
    }

    public Order handle(String orderId, UpdateOrderRequestDTO dto) {
        // 1. Fetch order from repository
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        double km = -1.0;
        String eta = "unknown";
        int etaMinutes = -1;

        if (dto.items() != null) {
            order.setItems(dto.items().stream()
                    .map(i -> new OrderItem(
                            i.productId(),
                            i.productName(),
                            i.productImage(),
                            i.unitPrice(),
                            i.quantity(),
                            i.promotionAmount(),
                            i.selectedSupplements() != null ?
                                    i.selectedSupplements().stream()
                                            .map(s -> new SupplementSelection(
                                                    s.supplementId(),
                                                    s.supplementName(),
                                                    s.quantity()
                                            ))
                                            .toList()
                                    : null
                    )).toList());
        }


            String clientLoc = order.getClient().getLatitude() + "," + order.getClient().getLongitude();
        String restLoc = order.getRestaurant().getLatitude() + "," + order.getRestaurant().getLongitude();

// ✅ Patch 1 : Vérification manuelle si les positions sont identiques
        if (clientLoc.equals(restLoc)) {
            log.warn("⚠️ Client and Restaurant have identical coordinates: {}. Forcing fallback values.", clientLoc);
            km = 0.0;
            eta = "1 min";
            etaMinutes = 1;
        } else {
            log.info("🧭 Querying Google Maps Distance Matrix: {} → {}", clientLoc, restLoc);
            Map<String, Object> mapResp = googleMapsService.getDistanceMatrix(clientLoc, restLoc);
            log.debug("🌐 Google Maps API raw response: {}", mapResp);

            if (mapResp != null && mapResp.containsKey("rows")) {
                List<Map<String, Object>> rows = (List<Map<String, Object>>) mapResp.get("rows");

                if (!rows.isEmpty()) {
                    List<Map<String, Object>> elements = (List<Map<String, Object>>) rows.get(0).get("elements");

                    if (elements != null && !elements.isEmpty()) {
                        Map<String, Object> elem = elements.getFirst();
                        String status = (String) elem.get("status");
                        log.info("🧪 Element status: {}", status);

                        if ("OK".equals(status)) {
                            Map<String, Object> distance = (Map<String, Object>) elem.get("distance");
                            Map<String, Object> duration = (Map<String, Object>) elem.get("duration");

                            if (distance != null && duration != null) {
                                km = ((Number) distance.get("value")).doubleValue() / 1000.0;
                                eta = (String) duration.get("text");
                                etaMinutes = parseEtaToMinutes(eta);

                                // ✅ Patch 2 : fallback au cas où km est 0.0 malgré un appel Google Maps réussi
                                if (km == 0.0) {
                                    log.warn("⚠️ Google Maps returned 0 km despite distinct locations. Fallback: 0.5 km");
                                    km = 0.5;
                                }

                                log.info("📍 Client → Restaurant distance: {} km (ETA: {}) (etaMinutes : {})", km, eta, etaMinutes);
                            } else {
                                log.warn("❌ Missing distance or duration in element: {}", elem);
                            }
                        } else {
                            log.warn("⚠️ Google Maps element status not OK: {}", status);
                        }
                    } else {
                        log.warn("⚠️ No elements found in Distance Matrix response row");
                    }
                } else {
                    log.warn("⚠️ Empty 'rows' in Google Maps response");
                }
            } else {
                log.warn("❌ No 'rows' in Distance Matrix response or response is null");
            }
        }


        OrderStatus currentStatus = order.getOrderStatus();

        if (dto.status() != null && (
                dto.status() == OrderStatus.REFUSED ||
                        dto.status() == OrderStatus.CANCELLED ||
                        dto.status() == OrderStatus.DELIVERED
        )) {
            order.setOrderStatus(dto.status());
            log.info("❌ Order {} explicitly set to status: {}", orderId, dto.status());
        } else {
            OrderStatus nextStatus = currentStatus.next();
            order.setOrderStatus(nextStatus);
            log.info("🔄 Order {} status advanced from {} to {}", orderId, currentStatus, nextStatus);
        }

// 👇 Récupère le nouveau statut après mise à jour
        OrderStatus nextStatus = order.getOrderStatus();
        if (nextStatus == OrderStatus.ACCEPTED) {
            log.info("✅ Order {} status set to ACCEPTED, sending notification to client...", orderId);
            String clientId = order.getClient().getClientId();
            Map<String, String> clientPayload = Map.of(
                    "orderId", orderId,
                    "message", "Your order has been accepted and is being prepared!"
            );
            messagingTemplate.convertAndSend(
                    "/topic/client/" + clientId + "/order-status",
                    clientPayload
            );
            log.info("🔔 Sent order-accepted notification to Client {} for Order {}", clientId, orderId);
        }

        if (nextStatus == OrderStatus.REFUSED) {
            log.info("✅ Order {} status set to REFUSED, sending notification to client...", orderId);
            String clientId = order.getClient().getClientId();
            Map<String, String> clientPayload = Map.of(
                    "orderId", orderId,
                    "message", "Your order has been refused we will contact you soon"
            );
            messagingTemplate.convertAndSend(
                    "/topic/client/" + clientId + "/order-status",
                    clientPayload
            );
            log.info("🔔 Sent order-accepted notification to Client {} for Order {}", clientId, orderId);
        }


        if (nextStatus == OrderStatus.DELIVERED) {
            log.info("📦 Order {} marked as DELIVERED, sending Kafka event...", orderId);

            Date deliveredAt = new Date(System.currentTimeMillis());
            Date acceptedAt = order.getDeliveryInfo().getAcceptedAt();

            long deliveryDurationMinutes = -1;

            if (acceptedAt != null) {
                deliveryDurationMinutes = TimeUnit.MILLISECONDS.toMinutes(
                        deliveredAt.getTime() - acceptedAt.getTime()
                );
                log.info("⏱️ Delivery Time for Order {} by Livreur {}: {} minutes",
                        orderId,
                        order.getDeliveryInfo().getDeliveryPersonId(),
                        deliveryDurationMinutes);
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

                    String livreurId = order.getDeliveryInfo().getDeliveryPersonId();
                    String alertMsg = String.format(
                            "🐌 Order %s was delivered LATE. ETA was %d min, but it took %d min.",
                            orderId, etaMinutes, deliveryDurationMinutes
                    );

                    messagingTemplate.convertAndSend(
                            "/topic/livreur/" + livreurId + "/late-alert",
                            alertMsg
                    );
                    log.warn("⚠️ Late delivery alert sent to Livreur {} for Order {}", livreurId, orderId);
                }
            }

            String deliveryPersonId = order.getDeliveryInfo().getDeliveryPersonId();
            Long assignmentCount = null;

            try {
                String assignmentKey = "livreur:stats:" + deliveryPersonId;
                Object countObj = redisTemplate.opsForHash().get(assignmentKey, "assignments");
                if (countObj != null) {
                    assignmentCount = Long.parseLong(countObj.toString());
                }
                log.info("📦 Livreur {} has been assigned {} times (from Redis)", deliveryPersonId, assignmentCount);
            } catch (Exception e) {
                log.warn("⚠️ Failed to fetch assignment count for livreur {}: {}", deliveryPersonId, e.getMessage());
            }
            log.info("📦 Order DELIVERED — sending event with distanceKm: {}", km);

            OrderDeliveredEvent event = OrderDeliveredEvent.builder()
                    .orderId(order.getId())
                    .total(order.getTotal())
                    .client(order.getClient())
                    .restaurant(order.getRestaurant())
                    .deliveryInfo(order.getDeliveryInfo())
                    .items(order.getItems())
                    .deliveredAt(new Date(System.currentTimeMillis()))
                    .distanceKm(km)
                    .deliveryDurationMinutes((double) deliveryDurationMinutes)
                    .deliveredOnTime(deliveredOnTime)
                    .assignmentCount(assignmentCount)
                    .build();

            deliveredOrderProducer.sendDeliveredEvent(event);
            String restaurantId = order.getRestaurant().getRestaurantId();

            Map<String, String> restaurantPayload = Map.of(
                    "orderId", orderId,
                    "message", "✅ La commande a été livrée avec succès au client."
            );

            messagingTemplate.convertAndSend(
                    "/topic/restaurant/" + restaurantId + "/order-status",
                    restaurantPayload
            );

            log.info("🔔 Notification de livraison envoyée au restaurant {} pour la commande {}", restaurantId, orderId);

            try {
                String livreurId = order.getDeliveryInfo().getDeliveryPersonId();
                redisTemplate.opsForValue().set("livreur:" + livreurId + ":status", "FREE");
                log.info("🔄 Livreur {} status set back to FREE after delivering order {}", livreurId, orderId);
            } catch (Exception e) {
                log.error("❌ Failed to update livreur status to FREE in Redis: {}", e.getMessage(), e);
            }
        }

        if (dto.livreurStatus() == LivreurOrderStatus.PICKED_UP) {
            String livreurId = order.getDeliveryInfo().getDeliveryPersonId();
            String clientId = order.getClient().getClientId(); // adjust based on your model

            // Notify the livreur to start tracking
            Map<String, String> livreurPayload = Map.of(
                    "orderId", orderId,
                    "intervalSec", "2"
            );
            messagingTemplate.convertAndSend(
                    "/topic/livreur/" + livreurId + "/start-tracking",
                    livreurPayload
            );
            log.info("▶️ Sent start-tracking to Livreur {} for Order {}", livreurId, orderId);

            // Notify the client that order is picked up and tracking is available
            Map<String, String> clientPayload = Map.of(
                    "orderId", orderId,
                    "message", "Your order has been picked up! You can now track your delivery driver."
            );
            messagingTemplate.convertAndSend(
                    "/topic/client/" + clientId + "/order-status",
                    clientPayload
            );
            log.info("🔔 Sent picked-up notification to Client {} for Order {}", clientId, orderId);
        }


        if (dto.isPrepaid() != null) {
            order.setPaymentStatus(dto.isPrepaid() ? ordering.domain.model.PaymentStatus.PAID : ordering.domain.model.PaymentStatus.PENDING);
        }

        if (dto.livreurStatus() != null) {
            order.setLivreurStatus(dto.livreurStatus());
        }

        if (nextStatus == OrderStatus.PREPARING) {
            String corr = UUID.randomUUID().toString();
            ClosestLivreurRequest request = new ClosestLivreurRequest(
                    order.getRestaurant().getLatitude(),
                    order.getRestaurant().getLongitude(),
                    corr
            );

            CompletableFuture<ClosestLivreursResponse> future = new CompletableFuture<>();
            responseAwaiter.register(corr, future);
            livreurProducer.sendRestaurantRequest(request);
            log.info("🕒 Waiting up to 5s for livreurs response for order {} (corrId={})", orderId, corr);

            int finalEtaMinutes = etaMinutes;
            future.orTimeout(5, TimeUnit.SECONDS)
                    .thenAccept(response -> {
                        int count = response.getLivreurs().size();
                        log.info("📣 Received {} livreurs for order {}", count, orderId);

                        if (count > 0) {
                            final var livreurs = new ArrayList<>(response.getLivreurs());
                            final int[] idx = {0};
                            final ScheduledFuture<?>[] scheduledTask = new ScheduledFuture<?>[1];

                            Runnable notifyNextLivreur = new Runnable() {
                                @Override
                                public void run() {
                                    if (idx[0] >= livreurs.size()) {
                                        log.info("🚫 No livreur accepted for order {}", orderId);
                                        scheduledTask[0].cancel(false);
                                        Map<String, String> moderatorPayload = Map.of(
                                                "orderId", orderId,
                                                "message", "Aucun livreur n'a accepté la commande. Une action est requise."
                                        );

                                        messagingTemplate.convertAndSend("/topic/moderator/orders", moderatorPayload);
                                        log.info("🔔 Notification envoyée au modérateur pour la commande {}", orderId);
                                        log.info("🛑 Scheduled task cancelled after all livreurs tried for Order {}", orderId);
                                        return;
                                    }

                                    var livreur = livreurs.get(idx[0]);

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

                                    log.info("📤 Notification sent to Livreur {} for Order {}: [Lat={}, Lng={}]",
                                            livreur.getLivreurId(),
                                            orderId,
                                            livreur.getLatitude(),
                                            livreur.getLongitude()
                                    );
                                    order.setLivreurStatus(LivreurOrderStatus.ASSIGNED);
                                    orderRepository.save(order);

                                    try {
                                        String assignmentKey = "livreur:stats:" + livreur.getLivreurId();
                                        Long newCount = redisTemplate.opsForHash().increment(assignmentKey, "assignments", 1);
                                        log.info("📊 Livreur {} assigned to order {} — total assignments now: {}",
                                                livreur.getLivreurId(), orderId, newCount);
                                    } catch (Exception e) {
                                        log.error("❌ Failed to increment and fetch assignment count in Redis for Livreur {}: {}",
                                                livreur.getLivreurId(), e.getMessage());
                                    }

                                    if (scheduledTask[0] != null && !scheduledTask[0].isDone()) {
                                        scheduledTask[0].cancel(false);
                                        log.info("🛑 Previous scheduled task cancelled for order {}", orderId);
                                    }

                                    scheduledTask[0] = scheduler.schedule(() -> {
                                        Order refreshedOrder = orderRepository.findById(orderId)
                                                .orElseThrow(() -> new RuntimeException("Order not found during acceptance check"));

                                        if (refreshedOrder.getLivreurStatus() == LivreurOrderStatus.ACCEPTED) {

                                            log.info("✅ Order {} accepted by Livreur {} phone number {}",
                                                    orderId, livreur.getLivreurId(), livreur.getPhoneNumber());

                                            DeliveryInfo deliveryInfo = DeliveryInfo.builder()
                                                    .deliveryPersonId(livreur.getLivreurId())
                                                    .deliveryPersonName(livreur.getFirstname() + " " + livreur.getLastname())
                                                    .contactNumber(livreur.getPhoneNumber())
                                                    .vehicleInfo(livreur.getMatricule())
                                                    .estimatedArrivalTime("unknown")
                                                    .acceptedAt(new Date(System.currentTimeMillis()))
                                                    .build();

                                            log.info("DeliveryInfo {}", deliveryInfo);
                                            refreshedOrder.setDeliveryInfo(deliveryInfo);
                                            orderRepository.save(refreshedOrder);

                                            try {
                                                String livreurId = deliveryInfo.getDeliveryPersonId();
                                                redisTemplate.opsForValue().set("livreur:" + livreurId + ":status", "BUSY");
                                                log.info("🔄 Livreur {} status set to BUSY after accepting order {}", livreurId, orderId);
                                            } catch (Exception e) {
                                                log.error("❌ Failed to update livreur status to BUSY in Redis: {}", e.getMessage(), e);
                                            }

                                            long etaMillisRaw = finalEtaMinutes > 0 ? finalEtaMinutes * 60L * 1000L : 0;
                                            final Date acceptedAt = deliveryInfo.getAcceptedAt();
                                            final Date etaDeadline = etaMillisRaw > 0 ? new Date(acceptedAt.getTime() + etaMillisRaw) : null;
                                            deliveryInfo.setEtaDeadline(etaDeadline);

                                            if (scheduledTask[0] != null && !scheduledTask[0].isDone()) {
                                                scheduledTask[0].cancel(false);
                                                log.info("🛑 Scheduled task cancelled after acceptance for Order {}", orderId);
                                            }
                                            else if (refreshedOrder.getDeliveryInfo().getDeliveryPersonId() != null){

                                                log.info("livreur accpeted");

                                            }

                                        } else if (refreshedOrder.getLivreurStatus() == LivreurOrderStatus.REFUSED) {
                                            log.info("❌ Livreur {} refused order {}, moving to next livreur", livreur.getLivreurId(), orderId);
                                            idx[0]++;
                                            scheduler.execute(this);  // Make sure 'this' is Runnable or replace accordingly
                                        } else {
                                            log.info("⏳ No acceptance after 50s for Livreur {}, moving to next...", livreur.getLivreurId());
                                            refreshedOrder.setLivreurStatus(LivreurOrderStatus.REFUSED);
                                            orderRepository.save(refreshedOrder);
                                            idx[0]++;
                                            scheduler.execute(this);  // Make sure 'this' is Runnable or replace accordingly
                                        }
                                    }, 50, TimeUnit.SECONDS);

                                }
                            };

                            scheduledTask[0] = scheduler.schedule(notifyNextLivreur, 0, TimeUnit.SECONDS);
                        } else {
                            log.warn("🚫 No livreurs available for order {}", orderId);
                            Map<String, String> moderatorPayload = Map.of(
                                    "orderId", orderId,
                                    "message", "Aucun livreur n'a accepté la commande. Une action est requise."
                            );

                            messagingTemplate.convertAndSend("/topic/moderator/orders", moderatorPayload);
                            log.info("🔔 Notification envoyée au modérateur pour la commande {}", orderId);
                        }
                    })
                    .exceptionally(ex -> {
                        log.error("🛑 Timeout or error fetching livreurs for order {}: {}", orderId, ex.getMessage());
                        return null;
                    });
        }

        return orderRepository.save(order);
    }

    private int parseEtaToMinutes(String eta) {
        if (eta == null || eta.isBlank()) return -1;

        eta = eta.toLowerCase();
        int minutes = 0;

        if (eta.contains("hour")) {
            String[] parts = eta.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].contains("hour")) {
                    minutes += Integer.parseInt(parts[i - 1]) * 60;
                } else if (parts[i].contains("min")) {
                    minutes += Integer.parseInt(parts[i - 1]);
                }
            }
        } else if (eta.contains("min")) {
            minutes += Integer.parseInt(eta.replaceAll("[^0-9]", ""));
        }
        return minutes;
    }
    public Order forceUpdateStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }
}*/