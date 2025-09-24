package restaurant.application.usecase.Ingredient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.domain.model.Ingredient;
import restaurant.domain.service.ProductDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetIngredientsByProductIdUseCase {

    private final ProductDomainService productDomainService;

    public List<IngredientResponseDTO> execute(String productId) {
        List<IngredientResponseDTO> ingredients = productDomainService.getIngredientByProductId(productId);

        return ingredients.stream()
                .map(i -> new IngredientResponseDTO(
                        i.getId(),
                        i.getName(),
                        i.getCategoryId(),
                        i.getCreatedby()
                ))
                .collect(Collectors.toList());
    }
}
