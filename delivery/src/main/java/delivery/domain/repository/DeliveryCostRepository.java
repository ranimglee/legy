package delivery.domain.repository;

import delivery.domain.model.DeliveryCost;

import java.util.Optional;

public interface DeliveryCostRepository {
    DeliveryCost save(DeliveryCost cost);
    Optional<DeliveryCost> findByOrderId(String orderId);
}
