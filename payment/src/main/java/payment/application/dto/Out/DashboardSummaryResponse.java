package payment.application.dto.Out;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponse {
    private double totalInvested;
    private double totalROI;
    private long totalInvestments;
    private String topInvestor;
}