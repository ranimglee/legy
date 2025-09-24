package restaurant.application.usecase.Menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Menu.MenuGroupedItemResponseDTO;
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
public class GetStructuredMenuUseCase {

    private final MenuDomainService menuDomainService;
    private final CategoryDomainService categoryDomainService;
    private final ProductDomainService productDomainService;

    public MenuResponseDTO execute(String menuId) {
        Optional<Menu> menuOpt = menuDomainService.getMenuById(menuId);
        if (menuOpt.isEmpty()) {
            throw new RuntimeException("Menu not found with id: " + menuId);
        }

        Menu menu = menuOpt.get();
        List<Category> categories = categoryDomainService.getAllCategories();

        List<MenuGroupedItemResponseDTO> items = menu.getCategories().stream()
                .map(cat -> {
                    List<Product> products = productDomainService.getProductsByCategoryId(cat.getId());

                    List<ProductResponseDTO> productDTOs = products.stream()
                            .map(p -> new ProductResponseDTO(
                                    p.getId(), p.getName(), p.getPricePostCom(), p.getDescription(), cat.getId(), null, null
                            ))
                            .collect(Collectors.toList());


                    return new MenuGroupedItemResponseDTO(cat.getId(), cat.getName(), productDTOs);
                })
                .filter(group -> !group.getProducts().isEmpty())
                .collect(Collectors.toList());

        System.out.println("========================>"+items);
        return new MenuResponseDTO(menu.getId(), menu.getName(), menu.getDescription(), items,menu.getRestaurantId());
    }
}
