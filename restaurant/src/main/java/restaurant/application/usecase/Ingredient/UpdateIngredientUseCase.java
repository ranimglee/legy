package restaurant.application.usecase.Ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientRequestDTO;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.domain.model.Ingredient;
import restaurant.domain.service.CategoryDomainService;
import restaurant.domain.service.IngredientDomainService;

@Service
@RequiredArgsConstructor
public class UpdateIngredientUseCase {

    private final IngredientDomainService ingredientDomainService;
    private final CategoryDomainService categoryDomainService;

    public IngredientResponseDTO execute(String id, IngredientRequestDTO request) {
        Ingredient ingredient = ingredientDomainService.getIngredientById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));

        // Optionnel : tu peux valider que la catégorie existe
        categoryDomainService.getCategoryById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        ingredient.setName(request.getName());
        ingredient.setCategoryId(request.getCategoryId()); // ✅ Met à jour le categoryId
        ingredient.setProductId(request.getProductId());

        Ingredient updated = ingredientDomainService.updateIngredient(ingredient);

        return new IngredientResponseDTO(
                updated.getId(),
                updated.getName(),
                updated.getCategoryId(), // ✅ utilise getCategoryId()
                updated.getCreatedby()
        );
    }
}
