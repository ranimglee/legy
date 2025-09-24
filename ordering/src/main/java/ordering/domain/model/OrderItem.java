package ordering.domain.model;

import lombok.*;
import ordering.application.dto.order.SupplementSelection;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderItem {
    private String productId;
    private String productName;
    private String productImageUrl;
    private Double unitPrice;
    private Integer quantity;
    private Double promotionAmount;

    private List<SupplementSelection> supplements;



    @Override
    public String toString() {
        return "OrderItem{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", supplements=" + supplements +
                '}';
    }


}
