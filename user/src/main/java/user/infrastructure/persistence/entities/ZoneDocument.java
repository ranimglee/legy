package user.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import shared.dto.RestaurantDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Document
public class ZoneDocument {
    @Id
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


