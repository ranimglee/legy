package user.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartProductEventDTO implements Serializable {

    @JsonProperty("guestSessionId")
    private String guestSessionId;

    @JsonProperty("products")
    private List<CartProduct> products;

    @JsonProperty("totalPrice")
    private double totalPrice;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartProduct implements Serializable {
        @JsonProperty("productId")
        private String productId;

        @JsonProperty("productName")
        private String productName;

        @JsonProperty("quantity")
        private int quantity;

        @JsonProperty("price")
        private double price;
    }
}
