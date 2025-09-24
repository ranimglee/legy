package ordering.domain.repository;


import ordering.domain.model.RefusalHistory;

import java.util.List;

public interface RefusalHistoryRepository {
    RefusalHistory save(RefusalHistory refusal);
    List<RefusalHistory> findByLivreurId(String livreurId);
}
