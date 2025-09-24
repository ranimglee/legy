package restaurant.application.usecase.Ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientRequestDTO;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.domain.model.Category;
import restaurant.domain.model.Ingredient;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;
import restaurant.domain.service.CategoryDomainService;
import restaurant.domain.service.IngredientDomainService;

@Service
@RequiredArgsConstructor
public class AddIngredientUseCase {

    private final IngredientDomainService ingredientDomainService;
    private final CategoryDomainService categoryDomainService;
    private final RestaurantRepository restaurantRepository;

    public IngredientResponseDTO execute(IngredientRequestDTO request) {
        Category category = categoryDomainService.getCategoryById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        Restaurant restaurant = restaurantRepository.findByCreatedBy(request.getCreatedby())
                .orElseThrow(() -> new RuntimeException("Restaurant not found for user: " + request.getCreatedby()));

        Ingredient ingredient = new Ingredient();
        ingredient.setName(request.getName());
        ingredient.setCategoryId(request.getCategoryId());
        ingredient.setCreatedby(request.getCreatedby());

        Ingredient saved = ingredientDomainService.addIngredient(ingredient);

        return new IngredientResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getCategoryId(),
                saved.getCreatedby());
    }


}