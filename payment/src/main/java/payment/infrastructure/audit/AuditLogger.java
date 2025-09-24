package payment.infrastructure.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Slf4j
@Component
public class AuditLogger {
    public void log(String action, String resourceId, LocalDateTime timestamp) {
        log.info("[AUDIT] Action: {}, ResourceID: {}, DateTime: {}",
                action, resourceId, timestamp);
    }
}