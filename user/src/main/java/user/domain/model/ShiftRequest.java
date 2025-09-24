package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftRequest {
    private String id;
    private String livreurId;
    private String zoneId;
    private Shift shift; // reference to Shift
    private ShiftStatus status;
}
