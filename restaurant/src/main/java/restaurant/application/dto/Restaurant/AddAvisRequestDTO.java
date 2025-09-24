package restaurant.application.dto.Restaurant;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAvisRequestDTO {

    @Min(1)
    @Max(5)
    private int score;

    @NotBlank
    private String comment;
}
