package ordering.infrastructure.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "payouts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayoutDocument {
    @Id
    private String id;

    private String restaurantId;
    private String restaurantName;

    private LocalDate payoutDate;

    private double totalRevenue;    // What restaurant receives (sum of pricePreCom)
    private double totalCommission; // What platform keeps

    private List<String> orderIds;
    private boolean isPaid;
}
