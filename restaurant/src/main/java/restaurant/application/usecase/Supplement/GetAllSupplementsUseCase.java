package restaurant.application.usecase.Supplement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.service.SupplementDomainService;
import restaurant.domain.model.Supplement;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllSupplementsUseCase {

    private final SupplementDomainService supplementDomainService;

    public List<SupplementResponseDTO> execute() {
        // Fetch all supplements from the domain service
        List<Supplement> supplements = supplementDomainService.getAllSupplements();

        // Map each supplement to a SupplementResponseDTO
        return supplements.stream()
                .map(supplement -> new SupplementResponseDTO(
                        supplement.getId(),
                        supplement.getName(),
                        supplement.getDescription(),
                        supplement.getPrice()))
                .collect(Collectors.toList());
    }
}
