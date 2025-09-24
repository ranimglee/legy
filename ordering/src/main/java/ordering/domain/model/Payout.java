package ordering.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payout {
    private String id;

    private String restaurantId;
    private String restaurantName;

    private LocalDate payoutDate;

    private double totalRevenue;
    private double totalCommission;

    private List<String> orderIds;
    private boolean paid;

}
