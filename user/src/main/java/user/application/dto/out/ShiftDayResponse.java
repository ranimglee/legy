package user.application.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import user.application.dto.in.ShiftDTO;

import java.util.List;

@Data
@AllArgsConstructor
public class ShiftDayResponse {
    private String day;
    private String date;
    private List<ShiftDTO> shifts;

}
