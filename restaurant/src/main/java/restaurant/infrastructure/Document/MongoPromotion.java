package restaurant.infrastructure.Document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("promotions")
public class MongoPromotion extends BaseAuditDocument {
    @Id
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
