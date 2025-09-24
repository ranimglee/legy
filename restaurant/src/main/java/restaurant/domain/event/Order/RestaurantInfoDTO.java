package restaurant.domain.event.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantInfoDTO {
    private String restaurantId;
    private Double commission;
    private Double revenueTotalCommission;
    private Integer nbrCommandesTotal;
    private String name;
    private String phone;
    private String address;
    private Double longitude;
    private Double latitude;
}
