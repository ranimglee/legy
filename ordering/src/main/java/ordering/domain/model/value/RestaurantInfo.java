package ordering.domain.model.value;

import lombok.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantInfo {

    private String restaurantId;
    private String name;
    private String phone;
    private String address;
    private Double commission;
    private Double latitude;
    private Double longitude;

    private Double revenueTotalCommission;
    private Integer nbrCommandesTotal;
    private String logo;


}
