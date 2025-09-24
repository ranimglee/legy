package ordering.adapters.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.LivreurStatusUpdateDTO;
import ordering.domain.dto.RefusalRequestDTO;
import ordering.domain.model.LivreurOrderStatus;
import ordering.domain.model.Order;
import ordering.domain.model.RefusalHistory;
import ordering.domain.model.RefusalReason;
import ordering.domain.repository.OrderRepository;
import ordering.domain.repository.RefusalHistoryRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LivreurController {

    private final OrderRepository orderRepository;
    private final RefusalHistoryRepository refusalHistoryRepository;

    @MessageMapping("/livreur/status")
    public void updateLivreurStatus(LivreurStatusUpdateDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setLivreurStatus(dto.getLivreurStatus());

        if (dto.getLivreurStatus() == LivreurOrderStatus.ACCEPTED) {
            log.info("✅ Livreur {} accepted order {}", dto.getLivreurId(), dto.getOrderId());
        }

        orderRepository.save(order);
    }

    @MessageMapping("/livreur/refuse")
    public void handleRefusal(RefusalRequestDTO dto) {
        RefusalHistory refusal = RefusalHistory.builder()
                .orderId(dto.getOrderId())
                .livreurId(dto.getLivreurId())
                .reason(dto.getReason())
                .refusedAt(Instant.now())
                .build();

        refusalHistoryRepository.save(refusal);
        log.info("📄 Refusal saved via WebSocket: {}", refusal);

        // 🔍 Count total refusals and OUT_OF_TIME refusals
        List<RefusalHistory> refusals = refusalHistoryRepository.findByLivreurId(dto.getLivreurId());
        long totalRefusals = refusals.size();
        long outOfTimeRefusals = refusals.stream()
                .filter(r -> r.getReason() == RefusalReason.OUT_OF_TIME)
                .count();

        log.info("📊 Refusal stats for Livreur {} — Total: {}, OUT_OF_TIME: {}",
                dto.getLivreurId(), totalRefusals, outOfTimeRefusals);
    }
}
