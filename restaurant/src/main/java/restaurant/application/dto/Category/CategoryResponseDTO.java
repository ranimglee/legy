package restaurant.application.dto.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private String id;
    private String name;
    private String createdby;
    private String restaurantId;



    public CategoryResponseDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
