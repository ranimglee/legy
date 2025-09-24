package restaurant.application.usecase.Supplement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.service.SupplementDomainService;

@Service
@RequiredArgsConstructor
public class DeleteSupplementUseCase {

    private final SupplementDomainService supplementDomainService;

    public void execute(String id) {
        supplementDomainService.deleteSupplement(id);
    }
}
