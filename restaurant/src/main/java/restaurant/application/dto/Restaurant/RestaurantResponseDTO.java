package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import restaurant.domain.model.*;
import shared.enums.AvailabilityStatus;



import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Data
@Getter
@Setter
@AllArgsConstructor

public class RestaurantResponseDTO {
    private String id;
    private String rib;
    private String idFisc;
    private String description;
    private boolean pickup;
    private String logo;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private double longitude;
    private double latitude;
    private AvailabilityStatus availability;
    private double averageRating;
    private int ratingCount;
    private String createdby;
    private double commission;
    private MainCuisineType mainCuisineType;
    private InternationalCuisine internationalCuisine;
    private List<OpeningHour> horaires;
    private Boolean isAssigned;
    private RestaurantStatus restaurantStatus;


    public RestaurantResponseDTO(String id, String nom, String adresse, String telephone, String email, double longitude, double latitude, Boolean isAssigned) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.longitude = longitude;
        this.latitude = latitude;
        this.isAssigned = isAssigned;
    }
}
