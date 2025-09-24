package ordering.infrastructure.Document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
@Setter
@Getter
@Document("issue_monthly_stats")
public class IssueMonthlyStats {
    private String month; // "2025-06"
    private double avgResponseTime;
    private double resolutionRate;
    private double deltaResponseTime;
    private double deltaResolutionRate;


}
