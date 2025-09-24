package shared.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRemovedFromZoneEvent {
    private String restaurantId;
    private String zoneId;
}