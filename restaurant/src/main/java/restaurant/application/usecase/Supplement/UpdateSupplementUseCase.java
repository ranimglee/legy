package restaurant.application.usecase.Supplement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Supplement.SupplementRequestDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.Supplement;
import restaurant.domain.service.SupplementDomainService;

@Service
public class UpdateSupplementUseCase {

    private final SupplementDomainService supplementDomainService;

    @Autowired
    public UpdateSupplementUseCase(SupplementDomainService supplementDomainService) {
        this.supplementDomainService = supplementDomainService;
    }

    public SupplementResponseDTO execute(String id, SupplementRequestDTO request) {
        // Fetch the supplement
        Supplement supplement = supplementDomainService.getSupplementById(id)
                .orElseThrow(() -> new RuntimeException("Supplement not found with id: " + id));

        // Set supplement fields
        supplement.setName(request.getName());
        supplement.setDescription(request.getDescription());
        supplement.setPrice(request.getPrice());

        // Save the updated supplement
        Supplement updatedSupplement = supplementDomainService.updateSupplement(supplement);

        // Return the response DTO
        return new SupplementResponseDTO(
                updatedSupplement.getId(),
                updatedSupplement.getName(),
                updatedSupplement.getDescription(),
                updatedSupplement.getPrice()
        );
    }
}
