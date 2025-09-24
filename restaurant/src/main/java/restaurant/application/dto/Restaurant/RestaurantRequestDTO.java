package restaurant.application.dto.Restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import shared.enums.AvailabilityStatus;

import java.util.List;

@Getter
@Setter
public class RestaurantRequestDTO {
   private String rib;

    @NotBlank(message = "L'identifiant fiscal est obligatoire.")
    private String idFisc;

    private String description;

    private boolean pickup;

    private String logo;

    @NotBlank(message = "Le nom est obligatoire.")
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire.")
    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire.")
    private String telephone;

    @NotBlank(message = "L'email est obligatoire.")
    private String email;

    private double longitude;
    private double latitude;

    private String createdby;

    private double commission;

    private AvailabilityStatus availability;

    /**
     * NEW: main cuisine category (SENEGALESE, INTERNATIONALE, SAINE, DESSERT)
     */
    @NotNull(message = "Le type de cuisine principal est obligatoire.")
    private MainCuisineType mainCuisineType;

    /**
     * NEW: only used if mainCuisineType == INTERNATIONALE
     * no @NotNull here, will be validated in your use-case
     */
    private InternationalCuisine internationalCuisine;
    private List<OpeningHourDTO> horaires;

}
