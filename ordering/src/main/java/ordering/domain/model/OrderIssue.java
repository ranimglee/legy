package ordering.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderIssue {
    private String id;
    private String orderId;
    private String userId;
    private IssueType type;
    private String description;
    private Instant reportedAt;
    private List<String> attachments;
    private String severity;
    private IssueResolutionStatus status;
    private Instant resolvedAt;
    private String resolvedBy; //moderatorId
    private String issueRef;
    private String solution;


}

