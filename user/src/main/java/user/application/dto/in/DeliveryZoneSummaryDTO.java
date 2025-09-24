package user.application.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryZoneSummaryDTO {
    private String zoneId;
    private String zoneName;
}