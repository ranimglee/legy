package restaurant.infrastructure.adapter.Rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import restaurant.application.dto.Restaurant.FinancialOrderReport;
import restaurant.domain.event.Order.GetOrdersEvent;
import restaurant.domain.event.Order.OrderDTO;
import restaurant.domain.repository.RestaurantRepository;
import restaurant.infrastructure.kafka.RestaurantToOrder.OrderEventConsumer;
import restaurant.domain.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/financial/restaurant")
public class OrderEventController {

    private final OrderEventConsumer orderEventConsumer;
    private final RestaurantRepository restaurantRepository;

    public OrderEventController(OrderEventConsumer orderEventConsumer,
                                RestaurantRepository restaurantRepository) {
        this.orderEventConsumer = orderEventConsumer;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/financial-report/{restaurantId}")
    public List<FinancialOrderReport> getFinancialReport(@PathVariable String restaurantId) {
        List<FinancialOrderReport> report = new ArrayList<>();

        // Fetch the restaurant info ONCE
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElse(null);

        if (restaurant == null) {
            throw new RuntimeException("Restaurant not found with ID: " + restaurantId);
        }

        // Iterate over all received events
        for (GetOrdersEvent event : orderEventConsumer.getReceivedEvents()) {
            if (!event.getRestaurantId().equals(restaurantId)) continue;

            for (OrderDTO order : event.getOrders()) {
                FinancialOrderReport entry = new FinancialOrderReport();

                entry.setRestaurantName(restaurant.getNom());
                entry.setRestaurantPhone(restaurant.getTelephone());
                entry.setCommission(restaurant.getCommission());
               entry.setRevenueTotalCommission(restaurant.getRevenueTotalCommission());
                entry.setNbrCommandesTotal(restaurant.getNbrCommandesTotal());

                entry.setOrderId(order.getId());
                entry.setLivreurStatus(order.getLivreurStatus() != null ? order.getLivreurStatus() : null);

                report.add(entry);
            }
        }

        return report;
    }
}
