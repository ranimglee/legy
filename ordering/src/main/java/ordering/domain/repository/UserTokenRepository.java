package ordering.domain.repository;

import ordering.domain.model.UserToken;
import ordering.infrastructure.Document.UserTokenDocument;
import org.apache.catalina.User;

import java.util.Optional;

public interface UserTokenRepository {
    Optional<String> findFCMTokenByUserId(String userId);
    UserToken saveToken(UserToken userToken);

    void delete(String id);

    Optional<UserToken> findByFcmToken(String cleanedToken);

    Optional<UserToken> findByUserId(String userId);
}