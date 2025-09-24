package delivery.domain.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeliveryCostCalculator {
    public double calculate(double distanceKm, double costPerKm) {
        return Math.round(distanceKm * costPerKm * 100.0) / 100.0;
    }
}