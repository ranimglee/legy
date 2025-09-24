package ordering.application.dto.order;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FeeBreakdown {
    double distanceFee;
    double weatherFee;
    double total;
}