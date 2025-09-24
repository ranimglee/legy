package ordering.infrastructure.repository;

import lombok.AllArgsConstructor;
import ordering.domain.model.UserToken;
import ordering.domain.repository.UserTokenRepository;
import ordering.infrastructure.Document.UserTokenDocument;
import ordering.infrastructure.mapper.UserTokenMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@AllArgsConstructor
public class UserTokenRepositoryImpl implements UserTokenRepository {

    private final MongoUserTokenRepository mongoUserTokenRepository;
    private final UserTokenMapper mapper;

    @Override
    public Optional<String> findFCMTokenByUserId(String userId) {
        return mongoUserTokenRepository.findByUserId(userId)
                .map(UserTokenDocument::getFcmToken);
    }

    @Override
    public UserToken saveToken(UserToken userToken) {
        return mapper.toDomain(
                mongoUserTokenRepository.save(mapper.toDocument(userToken))
        );    }

    @Override
    public void delete(String id) {
        mongoUserTokenRepository.deleteById(id);
    }

    @Override
    public Optional<UserToken> findByFcmToken(String cleanedToken) {
        return mongoUserTokenRepository.findByFcmToken(cleanedToken)
                .map(mapper::toDomain); // Use mapper to convert to UserToken
    }

    @Override
    public Optional<UserToken> findByUserId(String userId) {
        return  mongoUserTokenRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

}
