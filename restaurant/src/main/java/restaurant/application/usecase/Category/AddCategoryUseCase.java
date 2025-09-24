package restaurant.application.usecase.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Category.CategoryRequestDTO;
import restaurant.application.dto.Category.CategoryResponseDTO;

import restaurant.domain.model.Category;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;
import restaurant.domain.service.CategoryDomainService;

@Service
@RequiredArgsConstructor
public class AddCategoryUseCase {

    private final CategoryDomainService categoryDomainService;
    private final RestaurantRepository restaurantRepository;

    public CategoryResponseDTO execute(CategoryRequestDTO request) {
        Restaurant restaurant = restaurantRepository.findByCreatedBy(request.getCreatedby())
                .orElseThrow(() -> new RuntimeException("Restaurant not found for user: " + request.getCreatedby()));

        Category category = new Category();
        category.setName(request.getName());
        category.setCreatedby(request.getCreatedby());
        category.setRestaurantId(restaurant.getId());

        Category savedCategory = categoryDomainService.addCategory(category);

        return new CategoryResponseDTO(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getCreatedby(),
                savedCategory.getRestaurantId()
        );
    }
}