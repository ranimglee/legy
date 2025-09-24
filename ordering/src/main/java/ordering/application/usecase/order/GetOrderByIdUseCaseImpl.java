package ordering.application.usecase.order;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.OrderDetailsDTO;
import ordering.application.exception.OrderNotFoundException;
import ordering.application.mapper.OrderDetailsMapper;
import org.springframework.stereotype.Service;
import ordering.domain.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class GetOrderByIdUseCaseImpl implements GetOrderByIdUseCase {

    private final OrderRepository orderRepository;

    @Override
    public OrderDetailsDTO handle(String orderId, String clientId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    System.out.println("DEBUG: order.clientId = " + order.getClient().getClientId());
                    System.out.println("DEBUG: jwt clientId = " + clientId);

                    if (!order.getClient().getClientId().equals(clientId)) {
                        throw new OrderNotFoundException(orderId + " (unauthorized access)");
                    }
                    return OrderDetailsMapper.toDto(order);
                })
                .orElseThrow(() -> new OrderNotFoundException(orderId + " (not found)"));
    }

}
