package restaurant.application.usecase.Ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.domain.service.IngredientDomainService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchIngredientsUseCase {
    private final IngredientDomainService ingredientService;

    public List<IngredientResponseDTO> execute(String query, int page, int size) {
        return ingredientService.searchByNamePrefix(query, page, size)
                .stream()
                .map(i -> new IngredientResponseDTO(
                        i.getId(), i.getName(),
                        i.getCategoryId(),
                        i.getCreatedby()))
                .toList();
    }
}
