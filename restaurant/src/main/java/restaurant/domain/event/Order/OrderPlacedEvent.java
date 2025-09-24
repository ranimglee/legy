package restaurant.domain.event.Order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private String orderId;
    private List<OrderItem> items;
    private String restaurantId;
    private ClientInfo client;
    private OrderStatus orderStatus; // <-- Add OrderStatus enum here

    public enum OrderStatus {
        PENDING, ACCEPTED, PREPARING, PREPARED, REFUSED, CANCELLED, COMPLETED
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class OrderItem {
        private String productId;
        private int quantity;
        private String productName;
        private Double unitPrice;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ClientInfo {
        private String clientId;
        private String firstName;
        private String lastName;
        private String phone;
        private String address;
    }

    @Override
    public String toString() {
        return "OrderPlacedEvent{" +
                "orderId='" + orderId + '\'' +
                ", items=" + items +
                ", restaurantId='" + restaurantId + '\'' +
                ", client=" + client +
                '}';
    }
}
