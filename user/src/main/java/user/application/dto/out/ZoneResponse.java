package user.application.dto.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.dto.RestaurantDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZoneResponse {

    private String zoneId;
    private String zoneName;
    private int maxCapacity;
    private boolean enabled;
    private List<List<Double>> coordinates;
    private int nbrAssignedDrivers;
    private int nbrRestaurants;
    private String color;
    private List<RestaurantDTO> assignedRestaurants;



}