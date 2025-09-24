package restaurant.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.UserPreference;
import restaurant.domain.repository.UserPreferenceRepository;
import restaurant.infrastructure.Document.MongoUserPreference;
import restaurant.infrastructure.mapper.UserPreferenceMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPreferenceRepositoryImpl implements UserPreferenceRepository {

    private final SpringDataUserPreferenceRepository springRepo;

    @Override
    public Optional<UserPreference> findByUserId(String userId) {
        return springRepo.findByUserId(userId)
                .map(UserPreferenceMapper::toDomain);
    }

    @Override
    public UserPreference save(UserPreference pref) {
        MongoUserPreference m = UserPreferenceMapper.toMongo(pref);
        MongoUserPreference saved = springRepo.save(m);
        return UserPreferenceMapper.toDomain(saved);
    }

    @Override
    public void deleteByUserId(String userId) {
        springRepo.deleteByUserId(userId);
    }
}
