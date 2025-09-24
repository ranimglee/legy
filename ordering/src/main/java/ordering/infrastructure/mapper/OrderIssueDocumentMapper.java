package ordering.infrastructure.mapper;

import lombok.RequiredArgsConstructor;
import ordering.domain.model.OrderIssue;
import ordering.infrastructure.Document.OrderIssueDocument;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderIssueDocumentMapper {
    private final OrderDocumentMapper orderDocumentMapper;

    public OrderIssueDocument toDocument(OrderIssue issue) {
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

    public OrderIssue toDomain(OrderIssueDocument doc) {
        return OrderIssue.builder()
                .id(doc.getId())
                .orderId(doc.getOrderId()) // ✅ Convert to domain
                .userId(doc.getUserId())
                .type(doc.getType())
                .description(doc.getDescription())
                .reportedAt(doc.getReportedAt())
                .attachments(doc.getAttachments())
                .severity(doc.getSeverity())
                .status(doc.getStatus())
                .resolvedAt(doc.getResolvedAt())
                .resolvedBy(doc.getResolvedBy())
                .issueRef(doc.getIssueRef())
                .solution(doc.getSolution())
                .build();
    }
}
