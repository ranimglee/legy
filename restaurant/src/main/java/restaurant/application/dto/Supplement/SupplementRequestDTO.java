package restaurant.application.dto.Supplement;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplementRequestDTO {

    @NotBlank(message = "Le nom du supplément est requis.")
    private String name;

    @Min(value = 0, message = "Le prix doit être supérieur ou égal à 0.")
    private double price;

    @NotBlank(message = "La description du supplément est requise.")
    private String description;

    private String createdby;

}
