package restaurant.application.dto.Restaurant;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningHourDTO {
    private String day;
    private List<OpeningIntervalDTO> intervals;
    private boolean closed;
}
