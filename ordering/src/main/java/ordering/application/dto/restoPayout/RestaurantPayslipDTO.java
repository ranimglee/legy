package ordering.application.dto.restoPayout;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RestaurantPayslipDTO {
    private String reference;           // Unique reference
    private LocalDate generatedAt;      // Date when the payout was generated

    private String restaurantId;
    private String restaurantName;
    private LocalDate payoutDate;
    private double totalRevenue;
    private double totalCommission;
    private double netAmount; // = totalRevenue - totalCommission
    private boolean isPaid;
}