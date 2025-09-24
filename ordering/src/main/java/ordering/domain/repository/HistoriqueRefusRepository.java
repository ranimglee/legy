package ordering.domain.repository;

import ordering.domain.model.HistoriqueRefus;
import java.util.List;
import java.util.Optional;

public interface HistoriqueRefusRepository {
    HistoriqueRefus save(HistoriqueRefus historiqueRefus);

    Optional<HistoriqueRefus> findById(String id);

    List<HistoriqueRefus> findAll();
}
