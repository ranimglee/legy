package restaurant.application.dto.Ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientInfoDTO {
    private String ingredientId;
    private String name;
}
