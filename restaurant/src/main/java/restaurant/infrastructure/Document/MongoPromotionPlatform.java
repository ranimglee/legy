package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import shared.enums.PromotionType;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "platform_promotions")
public class MongoPromotionPlatform {

    @Id
    private String id;

    private String title;
    private String description;
    private PromotionType type;
    private double discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;

}