package delivery.infrastructure.persistence;


import delivery.domain.model.Order;
import delivery.domain.repository.DeliveryRepository;
import delivery.infrastructure.Document.MongoDelivery;

import delivery.infrastructure.mapper.DeliveryMapper;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final SpringDataOrderRepository repository;


    public DeliveryRepositoryImpl(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        MongoDelivery mongoOrder = DeliveryMapper.toMongo(order);
        MongoDelivery saved = repository.save(mongoOrder);
        return DeliveryMapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(String id) {
        return repository.findById(id)
                .map(DeliveryMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream()
                .map(DeliveryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Order> findByDeliveryPersonId(String deliveryPersonId) {
        List<MongoDelivery> mongoDeliveries = repository.findByDeliveryInfo_DeliveryPersonId(deliveryPersonId);
        return mongoDeliveries.stream()
                .map(DeliveryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MongoDelivery> findByDeliveryPersonIdAndDeliveredAtBetween(String deliveryPersonId, Date startDate, Date endDate) {
        return repository.findByDeliveryInfo_DeliveryPersonIdAndDeliveredAtBetween(deliveryPersonId, startDate, endDate);
    }


}

interface SpringDataOrderRepository extends MongoRepository<MongoDelivery, String> {
    List<MongoDelivery> findByDeliveryInfo_DeliveryPersonId(String deliveryPersonId);

    List<MongoDelivery> findByDeliveryInfo_DeliveryPersonIdAndDeliveredAtBetween(String deliveryPersonId, Date startDate, Date endDate);
}

