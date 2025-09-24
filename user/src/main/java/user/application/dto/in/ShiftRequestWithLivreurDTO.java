package user.application.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import user.domain.model.Shift;
@AllArgsConstructor
@Getter
@Setter
public class ShiftRequestWithLivreurDTO {
    private String shiftRequestId;
    private String status;
    private Shift shift;
    private LivreurDTO livreur;


}
