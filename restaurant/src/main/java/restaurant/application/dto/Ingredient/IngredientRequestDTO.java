package restaurant.application.dto.Ingredient;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientRequestDTO {

    @NotBlank(message = "Le nom de l'ingrédient est requis")
    private String name;

    @NotBlank(message = "L'ID de la catégorie est requis")
    private String categoryId;

    private String createdby;
    private String productId;

}
