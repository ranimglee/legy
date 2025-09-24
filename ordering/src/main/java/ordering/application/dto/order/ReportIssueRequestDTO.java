package ordering.application.dto.order;

import ordering.domain.model.IssueResolutionStatus;
import ordering.domain.model.IssueType;

import java.util.List;

public record ReportIssueRequestDTO(
        String issueRef,
        String orderId,
        String userId,
        IssueType type,
        String description,
        List<String> attachmentUrls,
        IssueResolutionStatus status


) {

}

