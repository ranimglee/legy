package restaurant.application.dto.Promotion;

import lombok.*;
import restaurant.application.dto.Product.ProductDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequestDTO {
    private String description;
    private double amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> productIds;
    private String targetProductId;
    private String imageUrl;
    private String restaurantId;

}