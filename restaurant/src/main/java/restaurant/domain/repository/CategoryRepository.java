package restaurant.domain.repository;

import restaurant.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);

    Optional<Category> findById(String id);

    List<Category> findAll();

    void deleteById(String id);

    List<Category> findTopN(int limit);

    List<Category> searchByNamePrefix(String q, int page, int size);
}
