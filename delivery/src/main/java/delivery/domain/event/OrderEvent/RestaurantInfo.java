package delivery.domain.event.OrderEvent;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Getter
@NoArgsConstructor
public class RestaurantInfo {

    private String restaurantId;
    private String name;
    private String phone;
    private String address;
    private double longitude;
    private double latitude;




    public RestaurantInfo(String restaurantId, String name, String phone, String address, double longitude, double latitude) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
