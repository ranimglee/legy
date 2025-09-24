package restaurant.application.dto.Product;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record ProductListDTO(List<ProductAllSummaryDTO> content) {}
