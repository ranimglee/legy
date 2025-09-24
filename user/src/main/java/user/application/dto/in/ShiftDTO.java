package user.application.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import user.domain.model.ShiftType;

import java.time.LocalTime;
import java.time.Duration;
@Data
@AllArgsConstructor
public class ShiftDTO {

    private String shiftId;
    private ShiftType shiftType;
    private LocalTime startTime;
    private LocalTime endTime;
    private Duration duration;
    private boolean available;
    private int maxCouriers;
    private int assignedCount;

}
