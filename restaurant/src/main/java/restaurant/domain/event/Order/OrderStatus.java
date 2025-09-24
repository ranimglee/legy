package restaurant.domain.event.Order;

public enum OrderStatus {
    PENDING,
    ACCEPTED,
    PREPARING,
    PREPARED,
    REFUSED,
    CANCELLED,
    COMPLETED
}