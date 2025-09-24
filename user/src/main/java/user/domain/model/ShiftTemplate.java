package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftTemplate {
    private String id;

    private ShiftType shiftType;

    private LocalTime startTime;

    private LocalTime endTime;

    private int maxCouriers;

}
