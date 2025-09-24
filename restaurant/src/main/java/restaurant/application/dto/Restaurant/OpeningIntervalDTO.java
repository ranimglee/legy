package restaurant.application.dto.Restaurant;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningIntervalDTO {
    private String start;
    private String end;
}
