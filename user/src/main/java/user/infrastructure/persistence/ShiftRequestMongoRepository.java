package user.infrastructure.persistence;

import io.micrometer.observation.ObservationFilter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import user.domain.model.ShiftRequest;
import user.domain.model.ShiftStatus;
import user.infrastructure.persistence.entities.ShiftRequestDocument;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface ShiftRequestMongoRepository extends MongoRepository<ShiftRequestDocument, String> {

    @Query("{ 'livreurId': ?0, 'shift.shiftId': ?1 }")
    Optional<ShiftRequestDocument> findByLivreurIdAndShift_ShiftId(String livreurId, String shiftId);

    List<ShiftRequestDocument> findByLivreurId(String livreurId);

    @Query("{ 'livreurId': ?0, 'shift.date': { $gte: ?1, $lte: ?2 } }")
    List<ShiftRequestDocument> findByLivreurIdAndShiftDateBetween(String livreurId, LocalDate start, LocalDate end);

    List<ShiftRequestDocument> findByLivreurIdAndShift_Date(String livreurId, LocalDate shiftDate);

    List<ShiftRequestDocument> findByShift_Date(LocalDate shiftDate);

    List<ShiftRequestDocument> findByShiftDateBetweenAndStatus(LocalDate weekStart, LocalDate weekEnd, ShiftStatus status);}