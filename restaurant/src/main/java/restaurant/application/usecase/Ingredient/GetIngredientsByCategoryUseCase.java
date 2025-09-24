package restaurant.application.usecase.Ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.domain.model.Ingredient;
import restaurant.domain.service.IngredientDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetIngredientsByCategoryUseCase {

    private final IngredientDomainService ingredientDomainService;

    public List<IngredientResponseDTO> execute(String categoryId) {
        return ingredientDomainService.getIngredientsByCategoryId(categoryId).stream()
                .map(i -> new IngredientResponseDTO(i.getId(),
                        i.getName(),
                        i.getCategoryId(),
                        i.getCreatedby()))
                .collect(Collectors.toList());
    }
}
