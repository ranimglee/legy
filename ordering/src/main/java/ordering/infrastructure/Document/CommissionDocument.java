package ordering.infrastructure.Document;

import lombok.*;
import ordering.domain.model.DeliveryMode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document("commissions")
public class CommissionDocument {
        @Id
        private String idCommission;
        private Instant lastModifiedAt;
        private Instant createdAt;
        private String createdBy;
        private String modifiedBy;
        private double deliveryCommissionPercentage;
        private double pickupCommissionPercentage;
        private DeliveryMode deliveryMode;



}
