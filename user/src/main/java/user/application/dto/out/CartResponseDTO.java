package user.application.dto.out;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponseDTO {
    private List<CartProductDTO> products;
    private double totalPrice;
}
