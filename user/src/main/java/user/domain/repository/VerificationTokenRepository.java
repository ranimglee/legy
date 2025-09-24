package user.domain.repository;

import user.domain.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    Optional<VerificationToken> findByToken(String token);

    VerificationToken save(VerificationToken token);

    void deleteById(String id);

    void delete(VerificationToken token);
}
