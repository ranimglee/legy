package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import restaurant.domain.model.MainCuisineType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSummaryDto {
    private String id;
    private String nom;
    private MainCuisineType mainCuisineType;
    private double commission;
    private double revenueTotalCommission;
}