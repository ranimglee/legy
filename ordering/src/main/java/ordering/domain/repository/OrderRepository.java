package ordering.domain.repository;

import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);


    List<Order> findAll();

    List<Order> findByClientIdOrderByCreatedAtDesc(String clientId, Pageable pageable);

    List<Order> findByClientIdAndOrderstatusOrderByCreatedAtDesc(String clientId, OrderStatus orderStatus, Pageable pageable);

    List<Order> findByRestaurantId(String restaurantId);

    List<Order> findCompletedOrdersByRestaurantId(String restaurantId);


    List<Order> findByOrderstatus(OrderStatus orderStatus);

    List<Order> findAllByDeliveryInfoDeliveryPersonIdAndCreatedAtBetween(String deliveryPersonId, Instant start, Instant end);

    List<Order> findByOrderStatusAndIncludedInPayout(OrderStatus orderStatus, boolean b);

    void saveAll(List<Order> unpaidOrders);
}
