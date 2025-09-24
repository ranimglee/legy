package ordering.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.time.Instant;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)

public class PromoCode {
    private final String id;
    private final String code;
    private final double discountValue; // e.g., 0.15 for 15%
    private final Instant startDate;
    private final Instant endDate;
    private final int maxUsage;
    private final int currentUsage;


}
