package restaurant.domain.model;

import lombok.*;
import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningHour {
    private DayOfWeek day;
    private List<OpeningInterval> intervals;
    private boolean closed;


}
