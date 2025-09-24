package user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shared.domain.repository.UserSharedRepository;
import shared.domain.model.UserEntity;
import shared.domain.model.LivreurEntity;
import user.infrastructure.mapper.SharedUserMapper;
import user.infrastructure.persistence.entities.MongoLivreurEntity;
import user.infrastructure.persistence.entities.MongoUserEntity;
import user.infrastructure.persistence.MongoUserRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserSharedRepositoryImpl implements UserSharedRepository {
    private final MongoUserRepository mongoRepo;

    @Override
    public Optional<UserEntity> findById(String userId) {
        return mongoRepo.findById(userId).map(this::mapToSharedDomain);
    }

    private UserEntity mapToSharedDomain(MongoUserEntity mongo) {
        if (mongo instanceof MongoLivreurEntity mongoLivreur) {
            return SharedUserMapper.toSharedDomain(mongoLivreur);
        } else {
            throw new IllegalArgumentException("Unsupported entity for shared access: " + mongo.getClass().getSimpleName());
        }
    }
}
