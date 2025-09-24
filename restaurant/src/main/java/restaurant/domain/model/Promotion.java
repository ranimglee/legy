package restaurant.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion extends BaseAuditDomain {
    private String id;
    private String description;
    private double amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> productIds;
    private String targetProductId;
    private String imageUrl;
    private String restaurantId;

}

