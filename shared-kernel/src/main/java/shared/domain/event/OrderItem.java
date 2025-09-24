package shared.domain.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private String productId;
    private String productName;  // Ensure productName is present
    private Double unitPrice;
    private Integer quantity;


    @Override
    public String toString() {
        return "OrderItem{productId='" + productId + "', productName='" + productName + "', unitPrice=" + unitPrice + ", quantity=" + quantity + "}";
    }
}
