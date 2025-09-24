package restaurant.application.usecase.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Category.CategoryResponseDTO;
import restaurant.domain.service.CategoryDomainService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchCategoriesUseCase {

    private final CategoryDomainService categoryService;

    public List<CategoryResponseDTO> execute(String query, int page, int size) {
        return categoryService.searchByNamePrefix(query, page, size)
                .stream()
                .map(c -> new CategoryResponseDTO(
                        c.getId(),
                        c.getName(),
                        c.getCreatedby(),
                        c.getRestaurantId()))
                .toList();
    }
}

