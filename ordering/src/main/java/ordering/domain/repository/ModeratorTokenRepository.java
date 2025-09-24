package ordering.domain.repository;

import io.micrometer.observation.ObservationFilter;
import ordering.domain.model.ModeratorToken;

import java.util.List;
import java.util.Optional;

public interface ModeratorTokenRepository {
    void saveToken(String moderatorId, String fcmToken);
    String getToken(String moderatorId);
    void deleteToken(String moderatorId);


    List<ModeratorToken> findAll();
}
