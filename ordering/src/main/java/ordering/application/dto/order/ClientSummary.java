package ordering.application.dto.order;

public record ClientSummary(String clientId, String firstName, String lastName, long orderCount) {}
