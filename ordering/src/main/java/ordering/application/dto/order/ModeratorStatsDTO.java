package ordering.application.dto.order;

public record ModeratorStatsDTO(
        String moderatorId,
        int totalIssuesHandled,
        int issuesResolved,
        double avgResolutionTimeHours,
        double resolutionRate
) {}
