package user.application.dto.out;

import lombok.Data;
import user.domain.model.ShiftStatus;
import user.domain.model.ShiftType;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShiftRequestResponseDTO {
    private String shiftRequestId;
    private String livreurId;
    private String zoneId;
    private String shiftId;
    private ShiftType shiftType;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ShiftStatus status;
}