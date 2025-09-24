package restaurant.domain.model;

import lombok.*;
import shared.enums.PromotionType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionPlatform {
    private String id;
    private String title;
    private String description;
    private PromotionType type;
    private double discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
