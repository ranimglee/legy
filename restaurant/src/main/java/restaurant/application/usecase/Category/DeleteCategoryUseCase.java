package restaurant.application.usecase.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.domain.service.CategoryDomainService;

@Service
public class DeleteCategoryUseCase {

    private final CategoryDomainService categoryDomainService;

    @Autowired
    public DeleteCategoryUseCase(CategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    public void execute(String id) {
        categoryDomainService.deleteCategory(id);
    }
}
