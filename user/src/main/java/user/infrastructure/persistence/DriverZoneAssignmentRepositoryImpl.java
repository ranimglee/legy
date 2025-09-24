package user.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import user.domain.model.DriverZoneAssignment;
import user.domain.model.UserEntity;
import user.domain.repository.DriverZoneAssignmentRepository;
import user.infrastructure.mapper.DriverZoneAssignmentMapper;
import user.infrastructure.persistence.entities.MongoDriverZoneAssignment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
public class DriverZoneAssignmentRepositoryImpl implements DriverZoneAssignmentRepository {

    private final MongoDriverZoneAssignmentRepository mongoRepo;



    @Override
    public DriverZoneAssignment save(DriverZoneAssignment assignment) {
        MongoDriverZoneAssignment mongoDriverZoneAssignment = DriverZoneAssignmentMapper.toMongo(assignment);
        MongoDriverZoneAssignment savedAssignment = mongoRepo.save(mongoDriverZoneAssignment);
        return DriverZoneAssignmentMapper.toDomain(savedAssignment);
    }



    @Override
    public Optional<DriverZoneAssignment> findById(String id) {
        Optional<MongoDriverZoneAssignment> mongoAssignment = mongoRepo.findById(id);
        return mongoAssignment.map(DriverZoneAssignmentMapper::toDomain);
    }




    @Override
    public long countByZoneIdAndAssignedTrue(String zoneId) {
        return mongoRepo.countByZoneIdAndAssignedIsTrue(zoneId);
    }



    @Override
    public Optional<DriverZoneAssignment> findByLivreurIdAndZoneId(String livreurId, String zoneId) {
        Optional<MongoDriverZoneAssignment> mongoAssignment =mongoRepo.findByLivreurIdAndZoneId(livreurId, zoneId);
        return mongoAssignment.map(DriverZoneAssignmentMapper::toDomain);
    }
    @Override
    public List<DriverZoneAssignment> findAllByLivreurId(String livreurId) {
        List<MongoDriverZoneAssignment> mongoAssignments = mongoRepo.findAllByLivreurId(livreurId);
        return mongoAssignments.stream()
                .map(DriverZoneAssignmentMapper::toDomain)
                .toList(); // Use .collect(Collectors.toList()) if you're on Java < 16
    }

    @Override
    public List<DriverZoneAssignment> findByZoneIdAndAssignedTrue(String zoneId) {
        List<MongoDriverZoneAssignment> mongoAssignments = mongoRepo.findByZoneIdAndAssignedTrue(zoneId);

        return mongoAssignments.stream()
                .map(DriverZoneAssignmentMapper::toDomain)
                .toList();    }

    @Override
    public List<DriverZoneAssignment> findByLivreurId(String livreurId) {
        List<MongoDriverZoneAssignment> mongoAssignments = mongoRepo.findByLivreurId(livreurId);
        return mongoAssignments.stream()
                .map(DriverZoneAssignmentMapper::toDomain)
                .collect(Collectors.toList());
    }


}