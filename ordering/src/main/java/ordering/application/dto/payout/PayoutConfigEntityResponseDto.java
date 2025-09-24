package ordering.application.dto.payout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayoutConfigEntityResponseDto {
    private double bonusAmount;
    private int bonusThreshold;
    private double penaltyPerRefusal;
    private double baseDeliveryFee;
    private double weatherFee;
    private double driverCostPerKm;
    private double clientCostPerKm;
}
