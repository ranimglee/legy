package ordering.domain.dto;

import lombok.*;
import ordering.domain.model.RefusalReason;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefusalRequestDTO {
    private String orderId;
    private String livreurId;
    private RefusalReason reason;
}