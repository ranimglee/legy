package user.application.dto.in;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftRequestDTO {
    @NotBlank
    private String livreurId;

    @NotBlank
    private String zoneId;
    @NotBlank
    private String shiftId;

    public ShiftRequestDTO(String shiftId) {
        this.shiftId = shiftId;
    }
}
