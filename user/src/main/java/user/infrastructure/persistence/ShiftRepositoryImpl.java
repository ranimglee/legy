package user.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import user.domain.model.Shift;
import user.domain.model.ShiftType;
import user.domain.repository.ShiftRepository;
import user.infrastructure.mapper.ShiftMapper;
import user.infrastructure.persistence.entities.ShiftDocument;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository

public class ShiftRepositoryImpl implements ShiftRepository {

    private MongoShiftRepository mongoShiftRepository;

    @Override
    public Optional<Shift> findById(String shiftId) {
        Optional<ShiftDocument> shiftDocument = mongoShiftRepository.findById(shiftId);
        return shiftDocument.map(ShiftMapper::toDomain); // No need to explicitly return null
    }



    @Override
    public Shift save(Shift shift) {
        ShiftDocument shiftDocument = ShiftMapper.toDocument(shift);
        ShiftDocument savedShiftDocument = mongoShiftRepository.save(shiftDocument);
        return ShiftMapper.toDomain(savedShiftDocument);
    }

    @Override
    public boolean existsByZoneIdAndDateAndShiftType(String zoneId, LocalDate date, ShiftType type) {
        return mongoShiftRepository.existsByZoneIdAndDateAndShiftType(zoneId, date, type);
    }


    @Override
    public List<Shift> findShiftsByZoneAndDateBetween(String zoneId, LocalDate weekStart, LocalDate weekEnd) {
        return mongoShiftRepository.findAllByZoneIdAndDateRange(zoneId, weekStart, weekEnd)
                .stream()
                .map(ShiftMapper::toDomain)
                .toList();
    }

    @Override
    public Set<ShiftType> findShiftTypesByZoneAndDate(String zoneId, LocalDate date) {
        return mongoShiftRepository.findByZoneIdAndDate(zoneId, date).stream()
                .map(ShiftDocument::getShiftType)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteAllById(List<String> collect) {
        mongoShiftRepository.deleteAllById(collect);
    }


}