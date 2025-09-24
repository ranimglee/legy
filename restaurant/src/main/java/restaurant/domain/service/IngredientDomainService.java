package restaurant.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Ingredient;
import restaurant.domain.repository.IngredientRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredientDomainService {

    private final IngredientRepository ingredientRepository;

    public Ingredient addIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> getIngredientById(String id) {
        return ingredientRepository.findById(id);
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(String id) {
        ingredientRepository.deleteById(id);
    }

    public List<Ingredient> getIngredientsByIds(List<String> ids) {
        return ingredientRepository.findAllById(ids);
    }

    public List<Ingredient> getIngredientsByCategoryId(String categoryId) {
        return ingredientRepository.findByCategoryId(categoryId);
    }
    public List<Ingredient> searchByNamePrefix(String q, int page, int size) {
        return ingredientRepository.searchByNamePrefix(q, page, size);
    }
}
