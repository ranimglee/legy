package ordering.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter
@Setter
public class HistoriqueRefus extends BaseAuditDomain {

    private String orderId;
    private String status;
    private LocalDateTime timestamp;
    private String message;


    public HistoriqueRefus(String orderId, String status, LocalDateTime timestamp, String message) {
        this.orderId = orderId;
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
    }


    public HistoriqueRefus() {

    }
}
