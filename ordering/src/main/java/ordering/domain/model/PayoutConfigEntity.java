package ordering.domain.model;

import lombok.*;


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