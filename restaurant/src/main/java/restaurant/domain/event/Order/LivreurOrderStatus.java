package restaurant.domain.event.Order;

public enum LivreurOrderStatus {
    UNASSIGNED,
    ASSIGNED,
    EN_ROUTE_TO_RESTAURANT,
    PICKED_UP,
    EN_ROUTE_TO_CUSTOMER,
    DELIVERED,
    FAILED
}
