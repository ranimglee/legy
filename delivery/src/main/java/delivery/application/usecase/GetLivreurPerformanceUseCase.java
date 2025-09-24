package delivery.application.usecase;

import delivery.application.dto.out.LivreurPerformanceDTO;
import delivery.domain.model.Order;
import delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLivreurPerformanceUseCase {

    private final DeliveryRepository deliveryRepository;

    public LivreurPerformanceDTO handle(String deliveryPersonId, String period) {
        List<Order> orders = deliveryRepository.findByDeliveryPersonId(deliveryPersonId);

        Instant threshold = switch (period.toLowerCase()) {
            case "daily" -> Instant.now().minus(1, ChronoUnit.DAYS);
            case "weekly" -> Instant.now().minus(7, ChronoUnit.DAYS);
            case "monthly" -> Instant.now().minus(30, ChronoUnit.DAYS);
            default -> null;
        };

        orders = orders.stream()
                .filter(o -> threshold == null || (o.getDeliveredAt() != null && o.getDeliveredAt().toInstant().isAfter(threshold)))
                .toList();

        // ⬇️ performance logic unchanged, using filtered list

        if (orders.isEmpty()) {
            return new LivreurPerformanceDTO(deliveryPersonId, 0.0, 0, 0.0, 0, 0.0, 0);
        }

        int assignmentCount = orders.get(orders.size() - 1).getAssignmentCount() != null
                ? Math.toIntExact(orders.get(orders.size() - 1).getAssignmentCount()) : 0;

        int totalDeliveries = 0;
        double totalDuration = 0;
        int onTimeDeliveries = 0;
        double onTimeDuration = 0;

        for (Order order : orders) {
            Double duration = order.getDeliveryDurationMinutes();
            if (duration != null && duration >= 0) {
                totalDeliveries++;
                totalDuration += duration;
                if (Boolean.TRUE.equals(order.getDeliveredOnTime())) {
                    onTimeDeliveries++;
                    onTimeDuration += duration;
                }
            }
        }

        double avgDuration = totalDeliveries > 0 ? totalDuration / totalDeliveries : 0.0;
        double avgOnTime = onTimeDeliveries > 0 ? onTimeDuration / onTimeDeliveries : 0.0;
        double successRate = assignmentCount > 0 ? ((double) totalDeliveries / assignmentCount) * 100 : 0.0;

        return new LivreurPerformanceDTO(
                deliveryPersonId,
                avgDuration,
                totalDeliveries,
                avgOnTime,
                onTimeDeliveries,
                successRate,
                assignmentCount
        );
    }


}
