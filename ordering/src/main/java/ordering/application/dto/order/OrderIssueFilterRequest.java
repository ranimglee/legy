package ordering.application.dto.order;

import lombok.Builder;
import lombok.Data;
import ordering.domain.model.IssueResolutionStatus;

import java.time.Instant;
@Data
@Builder
public class OrderIssueFilterRequest {
    private IssueResolutionStatus status;
    private String severity;
    private Instant startDate;
    private Instant endDate;
    private int page;
    private int size;

    // Getters or @Data if you prefer
    public IssueResolutionStatus getStatus() { return status; }
    public String getSeverity() { return severity; }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}