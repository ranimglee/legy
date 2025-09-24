package restaurant.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import shared.enums.AvailabilityStatus;

import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
public class Restaurant extends BaseAuditDomain {

    private String id;
    private String description;
    private String rib;
    private String idFisc;
    private boolean pickup;
    private String logo;
    @Indexed(unique = true)
    private String nom;
    private String adresse;
    @Indexed(unique = true)
    private String telephone;
    @Indexed(unique = true)
    private String email;
    private double longitude;
    private double latitude;
    private AvailabilityStatus availability;
    private double averageRating = 0.0;
    private int ratingCount = 0;
    private String createdby;
    private MainCuisineType mainCuisineType;
    private InternationalCuisine internationalCuisine;
    private Integer averagePreparingTime;
    private Double commission;
    private Double revenueTotalCommission;
    private Integer nbrCommandesTotal;
    private Long followerCount;
    private List<OpeningHour> horaires;

    private Boolean isAssigned;
    private String assignedZoneId;
    private RestaurantStatus restaurantStatus;

    private String managerId;



    public void setMainCuisineType(MainCuisineType mainType) {
        this.mainCuisineType = mainType;
        if (mainType != MainCuisineType.INTERNATIONALE) {
            this.internationalCuisine = null;
        }
    }


  public void setInternationalCuisine(InternationalCuisine subType) {
        if (subType == null) {
            this.internationalCuisine = null;
            return;
        }
        if (this.mainCuisineType != MainCuisineType.INTERNATIONALE) {
            throw new IllegalStateException(
                    "Le sous-type international ne peut être utilisé que si mainCuisineType est INTERNATIONALE"
            );
        }
        this.internationalCuisine = subType;
    }

    public void addRating(int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }
        double total = this.averageRating * this.ratingCount;
        total += score;
        this.ratingCount++;
        this.averageRating = total / this.ratingCount;
    }
    public Double getRevenueTotalCommission() {
        return revenueTotalCommission != null ? revenueTotalCommission : 0.0;
    }
    public Integer getNbrCommandesTotal() {
        return nbrCommandesTotal!= null ? nbrCommandesTotal :0;
}
    public void replaceRating(int oldScore, int newScore) {
        if (newScore < 1 || newScore > 5) {
            throw new IllegalArgumentException("Score must be 1–5");
        }
        double total = this.averageRating * this.ratingCount;
        total = total - oldScore + newScore;
        this.averageRating = total / this.ratingCount;
    }



}
