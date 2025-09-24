package user.domain.repository;

import user.domain.model.ShiftTemplate;

import java.util.List;
import java.util.Optional;

public interface ShiftTemplateRepository {
    List<ShiftTemplate> findAll();

    ShiftTemplate save(ShiftTemplate template);

    Optional<ShiftTemplate> findById(String templateId);


    void deleteAll();

    int count();

    boolean existsById(String templateId);

    void deleteById(String templateId);
}
