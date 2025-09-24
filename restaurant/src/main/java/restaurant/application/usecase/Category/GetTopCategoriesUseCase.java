package restaurant.application.usecase.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Category.CategoryResponseDTO;
import restaurant.domain.service.CategoryDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTopCategoriesUseCase {

    private final CategoryDomainService categoryDomainService;

    public List<CategoryResponseDTO> execute(int limit) {
        return categoryDomainService.getTopCategories(limit).stream()
                .map(c -> new CategoryResponseDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }
}
