package restaurant.application.dto.EvaluateProduct;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EvaluateProductRequest(
        @NotNull @Min(1) @Max(5)
        Integer rating,
        String comment
) {
}
