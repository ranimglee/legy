package user.application.dto.in;

import lombok.Data;
import user.domain.model.ShiftType;

import java.time.LocalTime;

@Data
public class UpdateShiftTemplateRequest {
    private ShiftType shiftType;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCouriers;
}