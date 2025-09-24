package user.application.dto.out;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class BatchShiftResult {
    private List<String> successfulShiftIds;
    private List<String> failedShiftIds;



    public BatchShiftResult(List<String> successfulShiftIds, List<String> failedShiftIds) {
        this.successfulShiftIds = successfulShiftIds;
        this.failedShiftIds = failedShiftIds;
    }
}
