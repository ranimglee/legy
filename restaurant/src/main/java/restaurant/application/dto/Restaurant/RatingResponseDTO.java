package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingResponseDTO {
    private double averageRating;
    private int ratingCount;
}
