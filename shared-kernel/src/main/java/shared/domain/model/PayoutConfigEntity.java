package shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutConfigEntity {
    private String id;
    private double bonusAmount;
    private int bonusThreshold;
    private double penaltyPerRefusal;
    private double baseDeliveryFee;
    private double weatherFee;
    private double driverCostPerKm;
    private double clientCostPerKm;
}
