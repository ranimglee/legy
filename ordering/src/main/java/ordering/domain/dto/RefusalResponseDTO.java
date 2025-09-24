package ordering.domain.dto;

import lombok.*;
import ordering.domain.model.RefusalReason;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefusalResponseDTO {
    private String id;
    private String orderId;
    private String livreurId;
    private RefusalReason reason;
    private Instant refusedAt;
}