package ordering.application.dto.orderIssue;

public record OrderIssueNotification(
        String issueRef,
        String title,
        String body,
        String userId,
        String clientName)
{}