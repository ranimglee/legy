package user.application.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ZoneRequest {
    @NotBlank()
    @Size(min = 2, max = 20)
    private String zoneName;
    @NotNull()
    private List<List<Double>> coordinates;

    @NotNull()
    private String color;
}