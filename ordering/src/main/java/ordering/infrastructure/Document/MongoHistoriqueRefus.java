package ordering.infrastructure.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ordering.domain.model.BaseAuditDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "historiqueRefus")
@Getter
@Setter
@AllArgsConstructor
public class MongoHistoriqueRefus extends BaseAuditDocument {
    @Id
    private String orderId;
    private String status;
    private LocalDateTime timestamp;
    private String message;



}
