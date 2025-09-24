package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverZoneAssignment {
    private String id;
    private String livreurId;
    private String zoneId;     // Reference to the zone
    private boolean assigned;  // Whether the driver is assigned to the zone

    private Double latitude;
    private Double longitude;

    public boolean hasValidLocation() {
        return latitude != null && longitude != null;
    }

}