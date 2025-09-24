package restaurant.application.dto.Restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSnapshotDto {
    private String id;
    private String zoneId;
    private String nom;
    private String adresse;
    private String email;
    private String telephone;
    private Double longitude;
    private Double latitude;
    private Boolean isAssigned;



}
