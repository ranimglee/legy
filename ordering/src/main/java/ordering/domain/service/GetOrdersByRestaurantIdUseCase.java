package ordering.domain.service;

import lombok.RequiredArgsConstructor;
import ordering.domain.model.Order;
import ordering.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrdersByRestaurantIdUseCase {

    private final OrderRepository orderRepository;

    public List<Order> handle(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }
}
