package ordering.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import ordering.domain.repository.OrderRepository;
import ordering.infrastructure.Document.OrderDocument;
import ordering.infrastructure.mapper.OrderDocumentMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final MongoOrderRepository mongoRepo;
    private final OrderDocumentMapper mapper;

    @Override
    public Order save(Order order) {
        return mapper.toDomain(
                mongoRepo.save(mapper.toDocument(order))
        );
    }

    @Override
    public Optional<Order> findById(String id) {
        return mongoRepo.findById(id)
                .map(mapper::toDomain);
    }



    public List<Order> findByClientIdOrderByCreatedAtDesc(String clientId, Pageable pageable) {
        return mapper.toDomainList(mongoRepo.findByClient_ClientIdOrderByCreatedAtDesc(clientId, pageable));
    }

    @Override
    public List<Order> findByClientIdAndOrderstatusOrderByCreatedAtDesc(String clientId, OrderStatus orderStatus, Pageable pageable) {
        return mapper.toDomainList(mongoRepo.findByClient_ClientIdAndStatusOrderByCreatedAtDesc(clientId, orderStatus, pageable));
    }

    @Override
    public List<Order> findAll() {
        return mapper.toDomainList(mongoRepo.findAll());
    }


    @Override
    public List<Order> findByRestaurantId(String restaurantId) {
        return mongoRepo.findByRestaurant_RestaurantId(restaurantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }


    @Override
    public List<Order> findCompletedOrdersByRestaurantId(String restaurantId) {
        List<OrderDocument> mongoOrders = mongoRepo.findByRestaurant_RestaurantIdAndStatus(restaurantId, OrderStatus.DELIVERED);
        return mapOrderDocumentsToDomain(mongoOrders);
    }

    @Override
    public List<Order> findByOrderstatus(OrderStatus orderStatus) {
        List<OrderDocument> mongoOrders = mongoRepo.findByStatus(OrderStatus.DELIVERED);

        return mapOrderDocumentsToDomain(mongoOrders);
    }

    @Override
    public List<Order> findAllByDeliveryInfoDeliveryPersonIdAndCreatedAtBetween(String deliveryPersonId, Instant start, Instant end) {
        List<OrderDocument> mongoOrders = mongoRepo.findAllByDeliveryInfoDeliveryPersonIdAndCreatedAtBetween(deliveryPersonId, start, end);

        return mapOrderDocumentsToDomain(mongoOrders);
    }

    @Override
    public List<Order> findByOrderStatusAndIncludedInPayout(OrderStatus orderStatus, boolean includedInPayout) {
        List<OrderDocument> documents = mongoRepo.findByStatusAndIncludedInPayout(orderStatus, includedInPayout);
        return mapOrderDocumentsToDomain(documents);
    }

    @Override
    public void saveAll(List<Order> orders) {
        List<OrderDocument> documents = orders.stream()
                .map(mapper::toDocument)
                .collect(Collectors.toList());

        mongoRepo.saveAll(documents);
    }


    // Helper method to avoid code repetition for mapping
    private List<Order> mapOrderDocumentsToDomain(List<OrderDocument> mongoOrders) {
        return mongoOrders.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }



}
