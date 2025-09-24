package restaurant.application.dto.Menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuRequestDTO {

    @NotBlank(message = "Le nom du menu est requis.")
    private String name;

    @NotBlank(message = "La description du menu est requise.")
    private String description;

    @NotEmpty(message = "Veuillez sélectionner au moins une catégorie pour le menu.")
    private List<String> categoryIds;

    @NotBlank(message = "Le restaurant  est requise.")
    private String restaurantId;

    private String createdby;

}
