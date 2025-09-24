package restaurant.domain.repository;


import restaurant.domain.model.UserPreference;

import java.util.Optional;

public interface UserPreferenceRepository {
    Optional<UserPreference> findByUserId(String userId);

    UserPreference save(UserPreference preference);

    void deleteByUserId(String userId);
}
