package ordering.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.OrderTrackingDTO;
import ordering.application.usecase.order.GetOrderTrackingUseCase;
import ordering.domain.model.Order;
import ordering.domain.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetOrderTrackingUseCaseImpl implements GetOrderTrackingUseCase {

    private final OrderRepository orderRepository;

    @Override
    public OrderTrackingDTO handle(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        return new OrderTrackingDTO(
                order.getId(),
                order.toTrackingStatus()
        );
    }
}
