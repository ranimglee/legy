package ordering.application.dto.orderIssue;

import java.util.List;

public record OrderIssueSummaryDTO(
        String id,
        String orderId,
        String type,
        String description,
        List<String> attachmentUrls,
        String status,
        String reportedAt ,
        String severity,
        String resolvedAt,
        String issueRef
) {}
