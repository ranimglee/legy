package delivery.domain.service;

import delivery.domain.model.Order;
import delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDeliveriesByPersonIdUseCase {

    private final DeliveryRepository deliveryRepository;

    public List<Order> handle(String deliveryPersonId) {
        return deliveryRepository.findByDeliveryPersonId(deliveryPersonId);
    }
}
