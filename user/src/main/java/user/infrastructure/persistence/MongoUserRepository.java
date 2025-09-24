package user.infrastructure.persistence;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import user.domain.model.Status;
import user.infrastructure.persistence.entities.MongoClientEntity;
import user.infrastructure.persistence.entities.MongoLivreurEntity;
import user.infrastructure.persistence.entities.MongoUserEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MongoUserRepository extends MongoRepository<MongoUserEntity, String> {
    Optional<MongoUserEntity> findByEmail(String email);

    boolean existsByUsername(String username);
    @Query("{ 'status': ?0, '_class': 'user.infrastructure.persistence.entities.MongoLivreurEntity' }")
    List<MongoLivreurEntity> findActiveLivreursExplicit(Status status);

    @Query("{ '_class': { $ne: 'user.infrastructure.persistence.entities.MongoModerateurEntity' } }")
    List<MongoUserEntity> findAllExceptModerateurs();
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<MongoUserEntity> findById(String userId);

    @Query(value = "{ status: 'ACTIVE' }", count = true)
    long countActiveUsers();


    Optional<MongoUserEntity> findByUsername(String username);
    List<MongoClientEntity> findByIdIn(List<String> userIds);

    @Query("{ '_class': 'user.infrastructure.persistence.entities.MongoLivreurEntity',  'status': ?0, 'isAssigned': { $in: [false, null] } }")
    List<MongoLivreurEntity> findByIsAssignedFalseOrNullAndStatus(Status status);

    @Aggregation(pipeline = {
            "{ $match: { '_class': 'user.infrastructure.persistence.entities.MongoLivreurEntity', '_id': { $in: ?0 }, 'status': ?1 } }"
    })
    List<MongoLivreurEntity> aggregateLivreursByIdInAndStatus(List<String> ids, Status status);



    @Query(value = "{ '_class': ?2, 'createdAt': { $gte: ?0, $lte: ?1 } }", count = true)
    Long countByCreatedAtBetweenAndClassName(Instant start, Instant end, String className);



}
