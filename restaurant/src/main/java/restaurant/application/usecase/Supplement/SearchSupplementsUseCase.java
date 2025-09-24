package restaurant.application.usecase.Supplement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.service.SupplementDomainService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchSupplementsUseCase {

    private final SupplementDomainService supplementDomainService;

    public List<SupplementResponseDTO> execute(String query, int page, int size) {
        return supplementDomainService.searchByNamePrefix(query, page, size)
                .stream()
                .map(s -> new SupplementResponseDTO(
                        s.getId(),
                        s.getName(),
                        s.getDescription(),
                        s.getPrice(),
                        s.getCreatedby(),
                        s.getRestaurantId()))
                .toList();
    }
}
