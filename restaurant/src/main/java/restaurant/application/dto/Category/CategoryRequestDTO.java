package restaurant.application.dto.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDTO {
    @NotBlank(message = "Le nom de la catégorie est requis")
    private String name;
    private String createdby;

}
