package restaurant.application.dto.Supplement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplementResponseDTO {

    private String id;
    private String name;
    private String description;
    private double price;
    private String createdby;
    private String restaurantId;



    // Constructor to convert Supplement to SupplementResponseDTO
    public SupplementResponseDTO(String id, String name,String description, double price ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

}
