package ordering.application.dto.order;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplementSelection {
    private String supplementId;
    private String supplementName;
    private int quantity;
}
