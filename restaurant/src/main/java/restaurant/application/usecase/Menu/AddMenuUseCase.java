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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddMenuUseCase {

    private final MenuDomainService menuDomainService;
    private final CategoryDomainService categoryDomainService;
    private final ProductDomainService productDomainService;

    public MenuResponseDTO execute(MenuRequestDTO request) {
        Menu menu = new Menu();
        menu.setName(request.getName());
        menu.setDescription(request.getDescription());

        // Link selected categories
        List<Category> categories = categoryDomainService.getAllCategories().stream()
                .filter(c -> request.getCategoryIds().contains(c.getId()))
                .collect(Collectors.toList());

        menu.setCategories(categories);
        menu.setRestaurantId(request.getRestaurantId());
        menu.setCreatedby(request.getCreatedby());
        Menu saved = menuDomainService.addMenu(menu);

        // Map to structured response with grouped products
        List<MenuGroupedItemResponseDTO> groupedItems = categories.stream()
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
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                groupedItems,
                saved.getRestaurantId(),
                saved.getCreatedby()

        );
    }
}
