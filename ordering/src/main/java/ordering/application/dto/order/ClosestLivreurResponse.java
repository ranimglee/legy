package ordering.application.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClosestLivreurResponse {
    private String livreurId;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String rib;
    private String matricule;
    private Double latitude;

    private Double longitude;

    @JsonProperty("distanceKm")
    private Double distance;
    private String correlationId;




}
