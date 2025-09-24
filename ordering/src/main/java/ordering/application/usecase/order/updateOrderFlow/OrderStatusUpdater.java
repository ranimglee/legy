package ordering.application.usecase.order.updateOrderFlow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.UpdateOrderRequestDTO;
import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusUpdater {

    public void updateStatus(Order order, UpdateOrderRequestDTO dto) {
        OrderStatus currentStatus = order.getOrderStatus();
        if (dto.status() != null && (
                dto.status() == OrderStatus.REFUSED ||
                        dto.status() == OrderStatus.CANCELLED ||
                        dto.status() == OrderStatus.DELIVERED
        )) {
            order.setOrderStatus(dto.status());
            log.info("❌ Order {} explicitly set to status: {}", order.getId(), dto.status());
        } else {
            OrderStatus nextStatus = currentStatus.next();
            order.setOrderStatus(nextStatus);
            log.info("🔄 Order {} status advanced from {} to {}", order.getId(), currentStatus, nextStatus);
        }
    }
}