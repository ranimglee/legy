package restaurant.application.usecase.Ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.domain.model.Ingredient;
import restaurant.domain.service.IngredientDomainService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetIngredientByIdUseCase {

    private final IngredientDomainService ingredientDomainService;

    public IngredientResponseDTO execute(String id) {
        Optional<Ingredient> optional = ingredientDomainService.getIngredientById(id);

        if (optional.isEmpty()) {
            throw new RuntimeException("Ingredient not found with id: " + id);
        }

        Ingredient i = optional.get();
        return new IngredientResponseDTO(i.getId(),
                i.getName(),
                i.getCategoryId(),  // ✅ remplacé ici
                i.getCreatedby());
    }
}
