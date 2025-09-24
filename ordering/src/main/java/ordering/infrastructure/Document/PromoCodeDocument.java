package ordering.infrastructure.Document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "promo_codes")
public class PromoCodeDocument {
    @Id
    private String id;

    private String code;
    private double discountValue;
    private Instant startDate;
    private Instant endDate;
    private  int maxUsage;
    private int currentUsage;
}
