package user.application.dto.in;

import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateShiftTimeRequest {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCouriers;
}