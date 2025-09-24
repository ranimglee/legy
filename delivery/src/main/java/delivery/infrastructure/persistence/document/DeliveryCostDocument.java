package delivery.infrastructure.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
@Document(collection = "delivery_costs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCostDocument {
    @Id
    private String id;
    private double distanceKm;
    private double costPerKm;
    private double totalCost;
    private String orderId;
    private Instant createdAt;
}
