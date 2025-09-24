package shared.domain.repository;

import shared.domain.model.UserEntity;

import java.util.Optional;

public interface UserSharedRepository {

    Optional<UserEntity> findById(String userId);

}
