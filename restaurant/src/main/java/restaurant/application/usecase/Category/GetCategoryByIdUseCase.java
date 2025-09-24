package restaurant.application.usecase.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Category.CategoryResponseDTO;
import restaurant.domain.model.Category;
import restaurant.domain.service.CategoryDomainService;

@Service
public class GetCategoryByIdUseCase {

    private final CategoryDomainService categoryDomainService;

    @Autowired
    public GetCategoryByIdUseCase(CategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    public CategoryResponseDTO execute(String id) {
        // Utilisation de orElseThrow pour récupérer la catégorie ou lancer une exception si non trouvé
        Category category = categoryDomainService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
