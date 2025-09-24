package ordering.application.usecase.order;

import lombok.AllArgsConstructor;
import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import ordering.domain.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shared.dto.ProductInfo;
import shared.port.ProductQueryPort;

import java.util.List;

@AllArgsConstructor
@Service
public class GetCompletedOrdersByRestaurantUseCase {

    private final OrderRepository orderRepository;
    private final ProductQueryPort productQueryPort;

    private static final Logger logger = LoggerFactory.getLogger(GetCompletedOrdersByRestaurantUseCase.class);


    /**
     * Fetches all completed orders for a given restaurant.
     *
     * @param restaurantId The ID of the restaurant to fetch orders for.
     * @return List of completed orders for the restaurant.
     */
    public List<Order> getCompletedOrdersByRestaurant(String restaurantId) {
        // Assuming there's a method in the repository to get completed orders for the restaurant
        return orderRepository.findCompletedOrdersByRestaurantId(restaurantId);
    }

    /**
     * Calculates the total commission revenue for a list of completed orders.
     *
     * @param orders The list of completed orders for the restaurant.
     * @return The total commission revenue for the restaurant.
     */
    public double calculateTotalCommissionRevenueForRestaurant(List<Order> orders) {
        double totalCommissionRevenue = 0.0;

        for (Order order : orders) {
            // Calculate the commission revenue for each completed order
            totalCommissionRevenue += calculateTotalCommissionRevenue(order);

        }

        return totalCommissionRevenue;
    }

    /**
     * Calculates the total commission revenue for a specific order.
     *
     * @param order The order for which commission revenue is calculated.
     * @return The commission revenue for the order.
     */
    private double calculateTotalCommissionRevenue(Order order) {
        // Only calculate commission for completed orders
        if (order == null || !OrderStatus.DELIVERED.equals(order.getOrderStatus())) {
            return 0.0;
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return 0.0;
        }

        double totalCommission = 0.0;

        for (var item : order.getItems()) {
            ProductInfo productInfo = productQueryPort.getProductById(item.getProductId());
            if (productInfo == null) {
                logger.error("Product not found for ID: {}", item.getProductId());
                continue;
            }

            double pricePreCommission = productInfo.getPricePreCom();
            double pricePostCommission = productInfo.getPricePostCom();

            double commissionPerUnit = pricePostCommission - pricePreCommission;
            double commissionForItem = commissionPerUnit * item.getQuantity();

            totalCommission += commissionForItem;

        }

        return totalCommission;
    }

    /**
     * Fetches all orders (regardless of status) for a given restaurant.
     */
    public List<Order> getAllOrdersForRestaurant(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }
}
