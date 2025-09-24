package restaurant.domain.repository;

import restaurant.domain.model.Ingredient;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findById(String id);

    List<Ingredient> findAll();

    void deleteById(String id);

    List<Ingredient> findAllById(List<String> ids);

    List<Ingredient> findByCategoryId(String categoryId);

    List<Ingredient> searchByNamePrefix(String q, int page, int size);
}
