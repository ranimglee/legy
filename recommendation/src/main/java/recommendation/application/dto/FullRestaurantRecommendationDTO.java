package recommendation.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FullRestaurantRecommendationDTO {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("adresse")
    private String adresse;

    @JsonProperty("availability")
    private String availability;

    @JsonProperty("averagePreparingTime")
    private Integer averagePreparingTime;

    @JsonProperty("averageRating")
    private Double averageRating;

    @JsonProperty("commission")
    private Double commission;

    @JsonProperty("createdby")
    private String createdBy;

    @JsonProperty("description")
    private String description;

    @JsonProperty("email")
    private String email;

    @JsonProperty("idFisc")
    private String idFisc;

    @JsonProperty("mainCuisineType")
    private String mainCuisineType;

    @JsonProperty("internationalCuisine")
    private String internationalCuisine;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("latitude")
    private Double latitude;


    @JsonProperty("location")
    private List<Double> location;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("pickup")
    private Boolean pickup;

    @JsonProperty("ratingCount")
    private int ratingCount;

}

