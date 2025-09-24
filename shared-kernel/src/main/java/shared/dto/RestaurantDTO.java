package shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDTO {
    private String id;
    private String zoneId;
    private String nom;
    private String adresse;
    private String email;
    private String telephone;
    private double longitude;
    private double latitude;
    private Boolean isAssigned;
}
