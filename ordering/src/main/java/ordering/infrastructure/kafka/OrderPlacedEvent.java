package ordering.infrastructure.kafka;

import lombok.Getter;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderPlacedEvent {

    private String orderId;
    private List<OrderItem> items;
    private String restaurantId;
    private ClientInfo client;
    private OrderStatus orderStatus;


    public enum OrderStatus {
        PENDING,
        ACCEPTED,
        PREPARING,
        PREPARED,
        REFUSED,
        CANCELLED,
        COMPLETED
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
}
