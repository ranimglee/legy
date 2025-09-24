package ordering.application.usecase.orderIssue;


import lombok.RequiredArgsConstructor;

import ordering.application.dto.orderIssue.OrderIssueSummaryDTO;
import ordering.domain.model.IssueResolutionStatus;
import ordering.domain.model.IssueType;
import ordering.domain.model.OrderIssue;
import ordering.domain.repository.OrderIssueRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyOrderIssuesUseCase {

    private final OrderIssueRepository repository;

    public List<OrderIssueSummaryDTO> handle(
            String userId,
            IssueResolutionStatus status,
            IssueType type,
            String from,
            String to
    ) {
        List<OrderIssue> all = repository.findAllByUserId(userId);

        return all.stream()
                .filter(issue -> status == null || issue.getStatus() == status)
                .filter(issue -> type == null || issue.getType() == type)
                .filter(issue -> {
                    if (from != null) {
                        Instant fromDate = Instant.parse(from);
                        return issue.getReportedAt().isAfter(fromDate);
                    }
                    return true;
                })
                .filter(issue -> {
                    if (to != null) {
                        Instant toDate = Instant.parse(to);
                        return issue.getReportedAt().isBefore(toDate);
                    }
                    return true;
                })
                .map(issue -> new OrderIssueSummaryDTO(
                        issue.getId(),
                        issue.getOrderId(),
                        issue.getType().name(),
                        issue.getDescription(),
                        issue.getAttachments(),
                        issue.getStatus().name(),
                        issue.getReportedAt().toString(),
                        issue.getSeverity(),
                        issue.getResolvedAt() != null ? issue.getResolvedAt().toString() : null,
                        issue.getIssueRef()
                ))
                .toList();
    }

}
