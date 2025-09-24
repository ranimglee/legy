package restaurant.application.dto.Supplement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplementInfoDTO {
    private String supplementId;
    private String name;
    private double price;
}
