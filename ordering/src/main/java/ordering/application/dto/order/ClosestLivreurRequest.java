package ordering.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClosestLivreurRequest {

    private Double latitude;
    private Double longitude;
    private String correlationId;



}

