package user.application.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DeliveryZoneResponseDto {
    private int nbrRestaurants;
    private int nbrAssignedDrivers;
    private int maxCapacity;


}
