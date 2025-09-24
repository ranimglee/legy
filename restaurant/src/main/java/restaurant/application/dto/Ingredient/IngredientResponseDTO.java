package restaurant.application.dto.Ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientResponseDTO {

    private String id;
    private String name;
    private String categoryId;
    private String createdby;



    public IngredientResponseDTO(String id, String name, String categoryId) {
        this.id=id;
        this.name=name;
        this.categoryId=categoryId;
    }



}
