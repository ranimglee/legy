package restaurant.application.usecase.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Category.CategoryRequestDTO;
import restaurant.application.dto.Category.CategoryResponseDTO;
import restaurant.domain.model.Category;
import restaurant.domain.service.CategoryDomainService;

@Service
public class UpdateCategoryUseCase {

    private final CategoryDomainService categoryDomainService;

    @Autowired
    public UpdateCategoryUseCase(CategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    public CategoryResponseDTO execute(String id, CategoryRequestDTO request) {
        // Utiliser Optional pour récupérer la catégorie
        Category category = categoryDomainService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Mettre à jour la catégorie
        category.setName(request.getName());

        // Sauvegarder la catégorie mise à jour
        Category updatedCategory = categoryDomainService.updateCategory(category);

        // Retourner la catégorie mise à jour sous forme de DTO
        return new CategoryResponseDTO(updatedCategory.getId(), updatedCategory.getName());
    }
}
