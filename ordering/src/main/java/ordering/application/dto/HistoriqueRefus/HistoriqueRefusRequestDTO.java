package ordering.application.dto.HistoriqueRefus;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HistoriqueRefusRequestDTO {
    private String orderId;
    private String status;
    private LocalDateTime timestamp;
    private String message;

    public HistoriqueRefusRequestDTO(String orderId, String status, LocalDateTime timestamp, String message) {
        this.orderId = orderId;
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
    }
}
