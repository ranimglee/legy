package restaurant.application.usecase.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.dto.Product.ProductRequestDTO;
import restaurant.application.dto.Product.ProductResponseDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.Category;
import restaurant.domain.model.Product;
import restaurant.domain.service.CategoryDomainService;
import restaurant.domain.service.ProductDomainService;

import java.util.stream.Collectors;

@Service
public class UpdateProductUseCase {

    private final ProductDomainService productDomainService;
    private final CategoryDomainService categoryDomainService;

    @Autowired
    public UpdateProductUseCase(ProductDomainService productDomainService, CategoryDomainService categoryDomainService) {
        this.productDomainService = productDomainService;
        this.categoryDomainService = categoryDomainService;
    }

    public ProductResponseDTO execute(String id, ProductRequestDTO request) {
        Product product = productDomainService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (request.getName() != null)
            product.setName(request.getName());

        if (request.getPrice() != null)
            product.setPricePostCom(request.getPrice());

        if (request.getDescription() != null)
            product.setDescription(request.getDescription());

        if (request.getCategoryId() != null)
            product.setCategoryId(request.getCategoryId());

        if (request.getStatus() != null)
            product.setStatus(request.getStatus());

        if (request.getAvailability() != null)
            product.setAvailability(request.getAvailability());

        if (request.getPreptime() != null)
            product.setPreptime(request.getPreptime());

        var supplementIds = request.getSupplementIds();
        var ingredientIds = request.getIngredientIds();

        if (request.getCreatedby() != null)
            product.setCreatedby(request.getCreatedby());

        product.setPricePostCom(request.getPricePreCom());

        Product updatedProduct = productDomainService.updateProduct(product, supplementIds, ingredientIds);

        var supplementDTOs = updatedProduct.getSupplements() != null
                ? updatedProduct.getSupplements().stream()
                .map(supp -> new SupplementResponseDTO(
                        supp.getId(),
                        supp.getName(),
                        supp.getDescription(),
                        supp.getPrice()))
                .toList()
                : null;

        return ProductResponseDTO.builder()
                .id(updatedProduct.getId())
                .restaurantId(updatedProduct.getRestaurantId())
                .name(updatedProduct.getName())
                .pricePostCom(updatedProduct.getPricePostCom())
                .description(updatedProduct.getDescription())
                .categoryId(updatedProduct.getCategoryId())
                .preptime(updatedProduct.getPreptime())
                .supplements(supplementDTOs)
                .ingredients(updatedProduct.getIngredients() != null
                        ? updatedProduct.getIngredients().stream()
                        .map(i -> new IngredientResponseDTO(
                                i.getId(), i.getName(), i.getCategoryId()))
                        .collect(Collectors.toList())
                        : null)
                .imageUrl(updatedProduct.getImageUrl())
                .status(updatedProduct.getStatus())
                .availability(updatedProduct.getAvailability())
                .build();
    }
}
