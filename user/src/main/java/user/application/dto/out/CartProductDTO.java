package user.application.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartProductDTO {
    private String productId;
    private String name;
    private int quantity;
    private double price;
}