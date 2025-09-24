package ordering.domain.model;

public enum LivreurOrderStatus {
    UNASSIGNED("Order not yet assigned to a livreur"),
    ASSIGNED("Order assigned to a livreur, awaiting acceptance"),
    ACCEPTED("Livreur accepted the order"),
    PICKED_UP("Livreur has picked up the order"),
    DELIVERED("Order delivered by livreur"),
    FAILED("Delivery failed"),
    REFUSED("Livreur refused the order"),
    PENDING("Order in pending state for livreur assignment");

    private final String description;

    LivreurOrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public LivreurOrderStatus next() {
        return switch (this) {
            case UNASSIGNED -> ASSIGNED;
            case ASSIGNED -> ACCEPTED;
            case ACCEPTED -> PICKED_UP;
            case PICKED_UP -> DELIVERED;
            case DELIVERED, FAILED, REFUSED, PENDING -> this;
        };
    }
}