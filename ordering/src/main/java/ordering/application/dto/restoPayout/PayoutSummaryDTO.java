package ordering.application.dto.restoPayout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayoutSummaryDTO {
    private String payoutId;
    private String restaurantName;
    private double totalRevenueToPay;
    private double totalCommission;
    private boolean isPaid;
    private LocalDate payoutDate;

}