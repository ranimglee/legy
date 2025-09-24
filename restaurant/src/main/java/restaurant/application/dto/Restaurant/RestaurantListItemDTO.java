package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import restaurant.domain.model.RestaurantStatus;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantListItemDTO {
    private String name;
    private String email;
    private String address;
    private RestaurantStatus restaurantStatus;
    private Double commission;
}