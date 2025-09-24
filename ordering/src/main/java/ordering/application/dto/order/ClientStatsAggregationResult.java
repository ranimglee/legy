package ordering.application.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientStatsAggregationResult {
    private long totalOrders;
    private double totalAmountSpent;
}