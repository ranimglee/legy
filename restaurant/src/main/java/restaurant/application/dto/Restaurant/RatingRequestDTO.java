package restaurant.application.dto.Restaurant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RatingRequestDTO {
    @Min(1)
    @Max(5)
    private int score;
}
