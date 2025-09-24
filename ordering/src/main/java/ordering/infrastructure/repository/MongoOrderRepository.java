package ordering.infrastructure.repository;


import ordering.domain.model.OrderStatus;
import ordering.infrastructure.Document.OrderDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MongoOrderRepository extends MongoRepository<OrderDocument, String> {

    List<OrderDocument> findByClient_ClientIdOrderByCreatedAtDesc(String clientId, Pageable pageable);

    List<OrderDocument> findByClient_ClientIdAndStatusOrderByCreatedAtDesc(String clientId, OrderStatus status, Pageable pageable);

    // Query by restaurantId (not restaurantInfo.id)
    List<OrderDocument> findByRestaurant_RestaurantId(String restaurantId);

    List<OrderDocument> findByRestaurant_RestaurantIdAndStatus(String restaurantId, OrderStatus orderStatus);

    List<OrderDocument> findByStatus(OrderStatus orderStatus);

    List<OrderDocument> findAllByDeliveryInfoDeliveryPersonIdAndCreatedAtBetween(String deliveryInfo_deliveryPersonId, Instant createdAt, Instant createdAt2);


    List<OrderDocument> findByStatusAndIncludedInPayout(OrderStatus orderStatus, boolean includedInPayout);
}
