package shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shared.enums.AvailabilityStatus;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo {
    private String productId;

    private String name;

    private double pricePreCom;
    private double pricePostCom;
    private String imageUrl;

    private AvailabilityStatus availability;
    private List<SupplementDTO> supplements;


}