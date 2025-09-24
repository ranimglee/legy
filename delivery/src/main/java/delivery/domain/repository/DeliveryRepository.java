// delivery.domain.repository.OrderRepository
package delivery.domain.repository;

import delivery.domain.model.Order;
import delivery.infrastructure.Document.MongoDelivery;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    void deleteById(String id);
    List<Order> findByDeliveryPersonId(String deliveryPersonId);

    List<MongoDelivery> findByDeliveryPersonIdAndDeliveredAtBetween(String deliveryPersonId, Date startDate, Date endDate);
}

