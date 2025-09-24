package ordering.application.dto.orderIssue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
public class ModeratorNotificationDTO {
    private String type;
    private String message;
    private Instant timestamp;

}
