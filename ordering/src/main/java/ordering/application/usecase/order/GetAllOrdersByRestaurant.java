package ordering.application.usecase.order;

import lombok.AllArgsConstructor;
import ordering.domain.model.Order;
import ordering.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class GetAllOrdersByRestaurant {

    private final OrderRepository orderRepository;  // Inject OrderRepository

    // Other methods...


    /**
     * Retrieves all orders for a specific restaurant.
     * @param restaurantId The ID of the restaurant.
     * @return A list of orders associated with the restaurant.
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrdersForRestaurant(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }
}
