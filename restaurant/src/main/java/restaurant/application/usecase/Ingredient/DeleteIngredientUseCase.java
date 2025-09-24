package restaurant.application.usecase.Ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.service.IngredientDomainService;

@Service
@RequiredArgsConstructor
public class DeleteIngredientUseCase {

    private final IngredientDomainService ingredientDomainService;

    public void execute(String id) {
        ingredientDomainService.deleteIngredient(id);
    }
}
