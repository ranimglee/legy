package restaurant.application.usecase.Menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Menu.MenuGroupedItemResponseDTO;
import restaurant.application.dto.Menu.MenuRequestDTO;
import restaurant.application.dto.Menu.MenuResponseDTO;
import restaurant.application.dto.Product.ProductResponseDTO;
import restaurant.domain.model.Category;
import restaurant.domain.model.Menu;
import restaurant.domain.model.Product;
import restaurant.domain.service.CategoryDomainService;
import restaurant.domain.service.MenuDomainService;
import restaurant.domain.service.ProductDomainService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateMenuUseCase {

    private final MenuDomainService menuDomainService;
    private final CategoryDomainService categoryDomainService;
    private final ProductDomainService productDomainService;

    public MenuResponseDTO execute(String id, MenuRequestDTO request) {
        Optional<Menu> menuOpt = menuDomainService.getMenuById(id);
        if (menuOpt.isEmpty()) {
            throw new RuntimeException("Menu not found with id: " + id);
        }

        Menu menu = menuOpt.get();
        menu.setName(request.getName());
        menu.setDescription(request.getDescription());

        List<Category> selectedCategories = categoryDomainService.getAllCategories().stream()
                .filter(c -> request.getCategoryIds().contains(c.getId()))
                .collect(Collectors.toList());

        menu.setCategories(selectedCategories);
        menu.setRestaurantId(request.getRestaurantId());

        Menu updated = menuDomainService.addMenu(menu);

        // Map to structured response with grouped products
        List<MenuGroupedItemResponseDTO> groupedItems = selectedCategories.stream()
                .map(category -> {
                    List<Product> products = productDomainService.getProductsByCategoryId(category.getId());
                    List<ProductResponseDTO> productDTOs = products.stream()
                            .map(p -> new ProductResponseDTO(
                                    p.getId(),
                                    p.getName(),
                                    p.getPricePostCom(),
                                    p.getDescription(),
                                    category.getId(),
                                    null,
                                    null
                            ))
                            .collect(Collectors.toList());

                    return new MenuGroupedItemResponseDTO(category.getId(), category.getName(), productDTOs);
                })
                .filter(group -> !group.getProducts().isEmpty())
                .collect(Collectors.toList());

        return new MenuResponseDTO(
                updated.getId(),
                updated.getName(),
                updated.getDescription(),
                groupedItems,
                updated.getRestaurantId()
        );
    }
}
