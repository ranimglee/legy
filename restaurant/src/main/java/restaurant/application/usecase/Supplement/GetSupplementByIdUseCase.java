package restaurant.application.usecase.Supplement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.Supplement;
import restaurant.domain.service.SupplementDomainService;

import java.util.Optional;

@Service
public class GetSupplementByIdUseCase {

    private final SupplementDomainService supplementDomainService;

    @Autowired
    public GetSupplementByIdUseCase(SupplementDomainService supplementDomainService) {
        this.supplementDomainService = supplementDomainService;
    }

    public SupplementResponseDTO execute(String id) {
        // Retrieve the supplement by id
        Optional<Supplement> supplementOptional = supplementDomainService.getSupplementById(id);

        if (supplementOptional.isEmpty()) {
            throw new RuntimeException("Supplement not found with id: " + id);
        }

        Supplement supplement = supplementOptional.get(); // Unwrap the Optional

        // Convert the supplement to a DTO and return
        return new SupplementResponseDTO(
                supplement.getId(),
                supplement.getName(),
                supplement.getDescription(),
                supplement.getPrice());
    }
}
