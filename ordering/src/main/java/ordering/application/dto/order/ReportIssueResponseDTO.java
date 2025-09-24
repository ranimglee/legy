package ordering.application.dto.order;

import ordering.domain.model.IssueResolutionStatus;
import ordering.domain.model.IssueType;

import java.time.Instant;
import java.util.List;

public record ReportIssueResponseDTO(
        String id,
        String orderId,
        String userId,
        IssueType type,
        String description,
        Instant reportedAt,
        List<String> attachmentUrls,
        IssueResolutionStatus status,
        Instant resolvedAt,
        String resolvedBy,
        String issueRef

) {
}
