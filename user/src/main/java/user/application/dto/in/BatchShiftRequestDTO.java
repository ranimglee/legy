package user.application.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class BatchShiftRequestDTO {
    private List<String> shiftIds;
    private List<LocalDate> daysOff;    // days the driver explicitly marks as OFF


}
