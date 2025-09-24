package restaurant.application.usecase.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Category.CategoryResponseDTO;
import restaurant.domain.model.Category;
import restaurant.domain.service.CategoryDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllCategoriesUseCase {

    private final CategoryDomainService categoryDomainService;

    @Autowired
    public GetAllCategoriesUseCase(CategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    public List<CategoryResponseDTO> execute() {
        List<Category> categories = categoryDomainService.getAllCategories();

        return categories.stream()
                .map(category -> new CategoryResponseDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }
}
