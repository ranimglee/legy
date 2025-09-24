package user.application.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AverageRating {
    private String livreurId;
    private double averageRating;
    private int totalEvaluations;
}
