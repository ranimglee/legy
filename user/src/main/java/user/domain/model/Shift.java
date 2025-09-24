package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Shift {

    private String shiftId;
    private int maxCouriers;
    private int assignedCount;
    private ShiftType shiftType;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> driverIds;
    private String zoneId;




    public boolean isAvailable() {

        return assignedCount < maxCouriers;
    }


    public Duration getDuration() {
        LocalDateTime startDateTime = date.atTime(startTime);
        LocalDateTime endDateTime = date.atTime(endTime);

        if (endTime.isBefore(startTime)) {
            // Crosses midnight → move end time to next day
            endDateTime = endDateTime.plusDays(1);
        }

        return Duration.between(startDateTime, endDateTime);
    }




    public boolean isValidTimeRange() {
        return getDuration().toMinutes() > 0;
    }


}
