package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shared.dto.RestaurantDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryZone {

    private String zoneId;
    private String zoneName;
    private List<List<Double>> coordinates;
    private int maxCapacity;
    private int nbrAssignedDrivers;
    private boolean enabled;
    private int nbrRestaurants;
    private String color;
    private List<RestaurantDTO> assignedRestaurants;


}
