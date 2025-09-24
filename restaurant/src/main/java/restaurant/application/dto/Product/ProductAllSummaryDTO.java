package restaurant.application.dto.Product;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ProductAllSummaryDTO(
        String id,
        String name,
        String imageUrl,
        double price,
        String description,
        double averageRating,
        int ratingCount
) {}
