package delivery.infrastructure.mapper;


import delivery.domain.model.Order;
import delivery.infrastructure.Document.MongoDelivery;

public class DeliveryMapper {

    public static MongoDelivery toMongo(Order order) {
        if (order == null) return null;

        return MongoDelivery.builder()
                .id(order.getId())
                .client(order.getClient())
                .restaurant(order.getRestaurant())
                .deliveryInfo(order.getDeliveryInfo())
                .items(order.getItems())
                .total(order.getTotal())
                .deliveredAt(order.getDeliveredAt())
                .distanceKm(order.getDistanceKm())
                .deliveryDurationMinutes(order.getDeliveryDurationMinutes())
                .deliveredOnTime(order.getDeliveredOnTime())
                .assignmentCount(order.getAssignmentCount())
                .build();
    }

    public static Order toDomain(MongoDelivery mongoOrder) {
        if (mongoOrder == null) return null;

        return Order.builder()
                .id(mongoOrder.getId())
                .client(mongoOrder.getClient())
                .restaurant(mongoOrder.getRestaurant())
                .deliveryInfo(mongoOrder.getDeliveryInfo())
                .items(mongoOrder.getItems())
                .total(mongoOrder.getTotal())
                .deliveredAt(mongoOrder.getDeliveredAt())
                .distanceKm(mongoOrder.getDistanceKm())
                .deliveryDurationMinutes(mongoOrder.getDeliveryDurationMinutes())
                .deliveredOnTime(mongoOrder.getDeliveredOnTime())
                .assignmentCount(mongoOrder.getAssignmentCount())
                .build();
    }
}


