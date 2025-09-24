package user.application.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import user.domain.model.Shift;

@Getter
@Setter
@AllArgsConstructor
public class WeeklyShiftRequestView {
    private String day;
    private String date;
    private String shiftType;
    private String status;
    private String timeRange;
    private int maxCouriers;
    private int assignedCount;

}
