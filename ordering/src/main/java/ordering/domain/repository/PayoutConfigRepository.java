package ordering.domain.repository;

import ordering.domain.model.PayoutConfigEntity;

import java.util.Optional;

public interface PayoutConfigRepository {
    Optional<PayoutConfigEntity> findById(String aDefault);

    PayoutConfigEntity save(PayoutConfigEntity defaults);

    boolean existsById(String id);

    void deleteById(String id);

    Iterable<PayoutConfigEntity> findAll();
}
