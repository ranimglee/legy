package ordering.application.usecase.order;

import lombok.AllArgsConstructor;
import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import ordering.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;


import java.time.format.TextStyle;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


@AllArgsConstructor
@Service
public class GetAllCompletedOrdersUseCase {

    private final OrderRepository orderRepository;
    public List<Order> getAllCompletedOrders() {
        return orderRepository.findByOrderstatus(OrderStatus.DELIVERED);
    }

    public double calculateTotalRevenueFromCompletedOrders(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getTotal)
                .sum();
    }

    /**
     * Calculates total commission revenue from completed orders.
     * Each order has a total and an associated restaurant commission percentage.
     *
     * @param completedOrders list of completed orders
     * @return total commission revenue
     */
    public double calculateTotalCommissionRevenueFromCompletedOrders(List<Order> completedOrders) {
        return completedOrders.stream()
                .mapToDouble(order -> {
                    double commission = (order.getRestaurant() != null && order.getRestaurant().getCommission() != null)
                            ? order.getRestaurant().getCommission()
                            : 0.0; // Default to 0.0 if null
                    return (commission / 100.0) * order.getTotal();
                })
                .sum();
    }

    public Map<String, Double> getMonthlyCommissionRevenue(List<Order> completedOrders) {
        Map<String, Double> monthlyRevenue = new TreeMap<>();

        for (Order order : completedOrders) {
            if (order.getCreatedAt() == null) continue;

            Month month = order.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .getMonth();
            int year = order.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .getYear();

            String monthYear = month.getDisplayName(TextStyle.SHORT, Locale.FRENCH) + " " + year;

            double commission = (order.getRestaurant().getCommission() / 100.0) * order.getTotal();
            monthlyRevenue.merge(monthYear, commission, Double::sum);
        }

        return monthlyRevenue;
    }

}
