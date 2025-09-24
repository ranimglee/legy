package delivery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCost {
    private String id;
    private double distanceKm;
    private double costPerKm;
    private double totalCost;
    private String orderId;
    private Instant createdAt;

    public DeliveryCost(double distanceKm, double costPerKm, double total, String orderId, Instant now) {
        this.distanceKm = distanceKm;
        this.costPerKm = costPerKm;
        this.totalCost = total;
        this.orderId = orderId;
        this.createdAt = now;
    }
}
