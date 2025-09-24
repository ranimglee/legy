package shared.domain.service;

import shared.domain.OrderNotification;

public interface NotificationService {
    void send(OrderNotification notification);
}
