package ordering.infrastructure.Document;

import lombok.*;
import ordering.domain.model.IssueResolutionStatus;
import ordering.domain.model.IssueType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.List;

@Document("orderIssues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderIssueDocument {
    @Id
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
    private String resolvedBy;
    private String solution;


    @Indexed(unique = true)
    private String issueRef;
    public static OrderIssueDocument fromDomain(ordering.domain.model.OrderIssue issue) {
        return OrderIssueDocument.builder()
                .id(issue.getId())
                .orderId(issue.getOrderId())
                .userId(issue.getUserId())
                .type(issue.getType())
                .description(issue.getDescription())
                .reportedAt(issue.getReportedAt())
                .attachments(issue.getAttachments())
                .severity(issue.getSeverity())
                .status(issue.getStatus())
                .resolvedAt(issue.getResolvedAt())
                .resolvedBy(issue.getResolvedBy())
                .issueRef(issue.getIssueRef())
                .solution(issue.getSolution())
                .build();
    }

    public ordering.domain.model.OrderIssue toDomain() {
        return ordering.domain.model.OrderIssue.builder()
                .id(this.id)
                .orderId(this.orderId)
                .userId(this.userId)
                .type(this.type)
                .description(this.description)
                .reportedAt(this.reportedAt)
                .attachments(this.attachments)
                .severity(this.severity)
                .status(this.status)
                .resolvedAt(this.resolvedAt)
                .resolvedBy(this.resolvedBy)
                .issueRef(this.issueRef)
                .solution(this.solution)
                .build();
    }

}
