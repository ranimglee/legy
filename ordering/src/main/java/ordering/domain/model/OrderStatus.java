package ordering.domain.model;

public enum OrderStatus {
    PENDING("Created, not yet confirmed by restaurant"),
    ACCEPTED("Restaurant accepted"),
    PREPARING("Food is being prepared"),
    PREPARED("Food is ready"),
    ASSIGNED("Assigned to a livreur"),
    REASSIGNED("Reassigned to another livreur"),
    DELIVERED("Order delivered and finalized"),
    REFUSED("Restaurant refused"),
    CANCELLED("Cancelled by client or system");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public OrderStatus next() {
        return switch (this) {
            case PENDING -> ACCEPTED;
            case ACCEPTED -> PREPARING;
            case PREPARING -> PREPARED;
            case PREPARED -> ASSIGNED;
            case ASSIGNED -> DELIVERED;
            case REASSIGNED -> DELIVERED;
            case DELIVERED, REFUSED, CANCELLED -> this;
        };
    }
}