package restaurant.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.OpeningHour;
import restaurant.domain.model.*;

import shared.enums.AvailabilityStatus;

import java.util.List;

@Document(collection = "restaurants")
@Getter
@Setter
public class MongoRestaurant extends BaseAuditDocument {

    @Id
    private String id;

    @TextIndexed
    private String nom;

    @TextIndexed
    private String adresse;

    @TextIndexed
    private String description;

    private String rib;
    private String idFisc;
    private boolean pickup;
    private String logo;

    private String telephone;
    private String email;

    private double longitude;
    private double latitude;

    private double averageRating = 0.0;
    private int ratingCount = 0;
    private double revenueTotalCommission;
    private int nbrCommandesTotal;
    private Long followerCount;
    private Boolean isAssigned;
    private String assignedZoneId;
    private RestaurantStatus restaurantStatus;
    private String managerId;

    /**
     * For geo queries, store [lon, lat] as a 2dsphere index.
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;
    private String createdby;


    // ---- new fields ----
    private MainCuisineType mainCuisineType;
    private InternationalCuisine internationalCuisine;
    private AvailabilityStatus availability;

    private Double commission;
    private List<OpeningHour> horaires;

}
