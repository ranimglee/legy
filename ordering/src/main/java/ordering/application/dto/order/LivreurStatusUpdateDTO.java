package ordering.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ordering.domain.model.LivreurOrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LivreurStatusUpdateDTO {
    private String orderId;
    private String livreurId;
    private LivreurOrderStatus livreurStatus; // e.g., ACCEPTED or REFUSED
}

