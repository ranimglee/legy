package ordering.application.usecase.HistoriqueRefus;

import ordering.application.dto.HistoriqueRefus.HistoriqueRefusRequestDTO;
import ordering.application.dto.HistoriqueRefus.HistoriqueRefusResponseDTO;
import ordering.domain.model.HistoriqueRefus;
import ordering.domain.service.HistoriqueRefusDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AddHistoriqueRefusUseCase {

    private final HistoriqueRefusDomainService historiqueRefusService;

    @Autowired
    public AddHistoriqueRefusUseCase(HistoriqueRefusDomainService historiqueRefusService) {
        this.historiqueRefusService = historiqueRefusService;
    }

    public HistoriqueRefusResponseDTO execute(HistoriqueRefusRequestDTO request) {
        HistoriqueRefus historiqueRefus = new HistoriqueRefus(
                request.getOrderId(),
                request.getStatus(),
                LocalDateTime.now(),
                request.getMessage()
        );

        HistoriqueRefus savedHistoriqueRefus = historiqueRefusService.addHistoriqueRefus(historiqueRefus);

        return new HistoriqueRefusResponseDTO(
                savedHistoriqueRefus.getOrderId(),
                savedHistoriqueRefus.getStatus(),
                savedHistoriqueRefus.getTimestamp(),
                savedHistoriqueRefus.getMessage()
        );
    }
}
