package ordering.infrastructure.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "payout_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutConfigDocument {
    @Id
    private String id;

    private double bonusAmount;
    private int bonusThreshold;
    private double penaltyPerRefusal;
    private double baseDeliveryFee;
    private double weatherFee;
    private double driverCostPerKm;
}