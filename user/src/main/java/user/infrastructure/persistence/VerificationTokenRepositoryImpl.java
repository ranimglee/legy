package user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import user.domain.model.VerificationToken;
import user.domain.repository.VerificationTokenRepository;
import user.infrastructure.mapper.VerificationTokenMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationTokenRepositoryImpl implements VerificationTokenRepository {

    private final MongoVerificationTokenRepository mongoRepo;

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return mongoRepo.findByToken(token).map(VerificationTokenMapper::toDomain);
    }

    @Override
    public VerificationToken save(VerificationToken token) {
        return VerificationTokenMapper.toDomain(
                mongoRepo.save(VerificationTokenMapper.toMongo(token))
        );
    }

    @Override
    public void deleteById(String id) {
        mongoRepo.deleteById(id);
    }

    @Override
    public void delete(VerificationToken token) {
        mongoRepo.deleteById(token.getId());
    }
}
