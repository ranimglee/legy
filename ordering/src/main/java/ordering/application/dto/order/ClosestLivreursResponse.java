package ordering.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClosestLivreursResponse {
    private String correlationId;
    private List<ClosestLivreurResponse> livreurs;
}
