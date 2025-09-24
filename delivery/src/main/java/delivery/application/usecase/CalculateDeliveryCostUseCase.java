package delivery.application.usecase;

import delivery.domain.model.DeliveryCost;
import delivery.domain.repository.DeliveryCostRepository;
import delivery.domain.service.DeliveryCostCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
@AllArgsConstructor
public class CalculateDeliveryCostUseCase {

    private final DeliveryCostRepository repository;
    private final DeliveryCostCalculator calculator = new DeliveryCostCalculator();



    public DeliveryCost execute(double distanceKm, double costPerKm, String orderId) {
        double total = calculator.calculate(distanceKm, costPerKm);
        DeliveryCost cost = new DeliveryCost(distanceKm, costPerKm, total, orderId, Instant.now());
        return repository.save(cost);
    }
}