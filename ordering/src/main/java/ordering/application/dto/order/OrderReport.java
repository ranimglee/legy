package ordering.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ordering.domain.model.BaseAuditDomain;
import ordering.domain.model.OrderStatus;
import ordering.domain.model.value.ClientInfo;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReport extends BaseAuditDomain {
    private String orderReportId;
    private String orderId;
    private ClientInfo client;
    private double totalAmount;
    private double commissionAmount;
    private double finalAmount;
    private OrderStatus status;
    private Instant createdAt;



    public OrderReport(String orderId, ClientInfo client, Instant createdAt, Double total, double commissionAmount, double finalAmount, OrderStatus orderstatus) {
        this.orderId = orderId;
        this.client = client;
        this.totalAmount = total;
        this.commissionAmount = commissionAmount;
        this.finalAmount = finalAmount;
        this.status = orderstatus;
        this.createdAt=createdAt;
    }
}
