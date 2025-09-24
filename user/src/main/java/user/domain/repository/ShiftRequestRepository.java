package user.domain.repository;

import user.domain.model.ShiftRequest;
import user.domain.model.ShiftStatus;
import user.infrastructure.persistence.entities.ShiftRequestDocument;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ShiftRequestRepository {
    Optional<ShiftRequest> findByLivreurIdAndShiftId(String livreurId, String shiftId);
    List<ShiftRequest> findByLivreurId(String livreurId);

    Optional<ShiftRequest> findById(String shiftRequestId);

    ShiftRequest save(ShiftRequest request);


    List<ShiftRequest> findByLivreurIdAndShiftDateBetween(String livreurId, LocalDate weekStart, LocalDate weekEnd);

    List<ShiftRequest> findByLivreurIdAndShiftDate(String authenticatedLivreurId, LocalDate shiftDate);

    void deleteById(String id);


    List<ShiftRequest> findAll();

    List<ShiftRequest> findByShiftDateBetweenAndStatus(LocalDate weekStart, LocalDate weekEnd, ShiftStatus shiftStatus);
}
