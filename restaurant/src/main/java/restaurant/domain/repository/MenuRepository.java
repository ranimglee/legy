package restaurant.domain.repository;

import restaurant.domain.model.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    Menu save(Menu menu);
    List<Menu> findAll();
    Optional<Menu> findById(String id);
    void deleteById(String id);
}
