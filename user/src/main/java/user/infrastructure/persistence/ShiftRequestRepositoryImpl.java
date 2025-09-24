package user.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import user.domain.model.ShiftRequest;
import user.domain.model.Shift;
import user.domain.model.ShiftStatus;
import user.domain.repository.ShiftRequestRepository;
import user.infrastructure.mapper.ShiftRequestMapper;
import user.infrastructure.persistence.entities.ShiftRequestDocument;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository

public class ShiftRequestRepositoryImpl implements ShiftRequestRepository {

    private final ShiftRequestMongoRepository shiftRequestMongoRepository;

    @Override
    public Optional<ShiftRequest> findByLivreurIdAndShiftId(String livreurId, String shiftId) {
        return shiftRequestMongoRepository.findByLivreurIdAndShift_ShiftId(livreurId, shiftId)
                .map(ShiftRequestMapper::toDomain);
    }

    @Override
    public List<ShiftRequest> findByLivreurId(String livreurId) {
        return shiftRequestMongoRepository.findByLivreurId(livreurId).stream()
                .map(ShiftRequestMapper::toDomain)
                .collect(Collectors.toList());
    }



    @Override
    public Optional<ShiftRequest> findById(String shiftRequestId) {
        return shiftRequestMongoRepository.findById(shiftRequestId)
                .map(ShiftRequestMapper::toDomain);
    }

    @Override
    public ShiftRequest save(ShiftRequest request) {
        ShiftRequestDocument document = ShiftRequestMapper.toDocument(request);
        ShiftRequestDocument saved = shiftRequestMongoRepository.save(document);
        return ShiftRequestMapper.toDomain(saved);
    }


    @Override
    public List<ShiftRequest> findByLivreurIdAndShiftDateBetween(String livreurId, LocalDate weekStart, LocalDate weekEnd) {
        return shiftRequestMongoRepository.findByLivreurIdAndShiftDateBetween(livreurId,weekStart,weekEnd).stream()
                .map(ShiftRequestMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftRequest> findByLivreurIdAndShiftDate(String livreurId, LocalDate shiftDate) {
        return shiftRequestMongoRepository.findByLivreurIdAndShift_Date(livreurId, shiftDate).stream()
                .map(ShiftRequestMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        shiftRequestMongoRepository.deleteById(id);
    }

    @Override
    public List<ShiftRequest> findAll() {
        return shiftRequestMongoRepository.findAll()
                .stream()
                .map(ShiftRequestMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftRequest> findByShiftDateBetweenAndStatus(LocalDate weekStart, LocalDate weekEnd, ShiftStatus shiftStatus) {
        return shiftRequestMongoRepository.findByShiftDateBetweenAndStatus(weekStart, weekEnd, shiftStatus)
                .stream()
                .map(ShiftRequestMapper::toDomain)
                .collect(Collectors.toList());
    }


}
