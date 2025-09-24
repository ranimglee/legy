package ordering.domain.model;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefusalHistory {
    private String id;
    private String orderId;
    private String livreurId;
    private RefusalReason reason;
    private Instant refusedAt;
}