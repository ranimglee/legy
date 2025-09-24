package ordering.domain.service;

import ordering.domain.model.HistoriqueRefus;
import ordering.domain.repository.HistoriqueRefusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistoriqueRefusDomainService {

    private final HistoriqueRefusRepository historiqueRefusRepository;

    public HistoriqueRefusDomainService(HistoriqueRefusRepository historiqueRefusRepository) {
        this.historiqueRefusRepository = historiqueRefusRepository;
    }

    public HistoriqueRefus addHistoriqueRefus(HistoriqueRefus historiqueRefus) {
        return historiqueRefusRepository.save(historiqueRefus);
    }

    public Optional<HistoriqueRefus> getHistoriqueRefusById(String id) {
        return historiqueRefusRepository.findById(id);
    }

    public List<HistoriqueRefus> getAllHistoriqueRefus() {
        return historiqueRefusRepository.findAll();
    }
}
