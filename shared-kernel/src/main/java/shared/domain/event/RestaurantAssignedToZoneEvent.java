package shared.domain.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import shared.dto.RestaurantDTO;

import java.util.List;

@Data
@NoArgsConstructor
public class RestaurantAssignedToZoneEvent {
    private String restaurantId;
    private String zoneId;
    private String name;
    private String address;
    private String email;
    private String telephone;
    private double longitude;
    private double latitude;


    public RestaurantAssignedToZoneEvent(String restaurantId, String zoneId, String name, String address, String email, String telephone, double longitude, double latitude) {
        this.restaurantId = restaurantId;
        this.zoneId = zoneId;
        this.name = name;
        this.address = address;
        this.email = email;
        this.telephone = telephone;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public RestaurantAssignedToZoneEvent(String restaurantId, String zoneId) {
        this.restaurantId=restaurantId;
        this.zoneId=zoneId;
    }
}
