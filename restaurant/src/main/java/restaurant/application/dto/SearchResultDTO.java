package restaurant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchResultDTO {
    private String title;
    private String description;
    private String type;
    private String id;
}
