package restaurant.application.dto.Restaurant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopRestaurantDTO {
    private String id;
    private String name;
    private long orderCount;
    private double averageRating;
}