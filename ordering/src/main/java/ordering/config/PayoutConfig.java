package ordering.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payout")
public class PayoutConfig {

    private double bonusAmount = 25.0;
    private int bonusThreshold = 30;
    private double penaltyPerRefusal = 2.0;
    private double baseDeliveryFee = 3.5;

}
