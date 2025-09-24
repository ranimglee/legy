package restaurant.application.dto.Menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponseDTO {

    private String id;
    private String name;
    private String description;
    private List<MenuGroupedItemResponseDTO> items;

    private String restaurantId;

    private String createdby;



    public MenuResponseDTO(String id, String name, String description, List<MenuGroupedItemResponseDTO> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.items = items;
    }
    public MenuResponseDTO(String id, String name, String description, List<MenuGroupedItemResponseDTO> items, String restaurantId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.items = items;
        this.restaurantId = restaurantId;
    }
}
