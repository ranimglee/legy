package ordering.application.usecase.HistoriqueRefus;

import ordering.application.dto.HistoriqueRefus.HistoriqueRefusResponseDTO;
import ordering.domain.model.HistoriqueRefus;
import ordering.domain.service.HistoriqueRefusDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllHistoriqueRefusUseCase {

    private final HistoriqueRefusDomainService historiqueRefusService;

    @Autowired
    public GetAllHistoriqueRefusUseCase(HistoriqueRefusDomainService historiqueRefusService) {
        this.historiqueRefusService = historiqueRefusService;
    }

    public List<HistoriqueRefusResponseDTO> execute() {
        List<HistoriqueRefus> historiques = historiqueRefusService.getAllHistoriqueRefus();

        return historiques.stream()
                .map(historique -> new HistoriqueRefusResponseDTO(
                        historique.getOrderId(),
                        historique.getStatus(),
                        historique.getTimestamp(),
                        historique.getMessage()
                ))
                .collect(Collectors.toList());
    }
}
