package restaurant.domain.model;

import lombok.*;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningInterval {
    private LocalTime start;
    private LocalTime end;
}
