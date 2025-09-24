package restaurant.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Ingredient.IngredientInfoDTO;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.dto.Supplement.SupplementInfoDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.Category;
import restaurant.domain.model.Ingredient;
import restaurant.domain.model.Product;
import restaurant.domain.model.Supplement;
import restaurant.domain.repository.CategoryRepository;
import restaurant.domain.repository.IngredientRepository;
import restaurant.domain.repository.ProductRepository;
import restaurant.domain.repository.SupplementRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDomainService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplementRepository supplementRepository;
    private final IngredientRepository ingredientRepository;
    private final CloudinaryService cloudinaryService;

    public Product addProduct(Product product, List<String> supplementIds, List<String> ingredientIds) {
        Optional<Category> category = categoryRepository.findById(product.getCategoryId());

        if (category.isPresent()) {
            if (supplementIds != null && !supplementIds.isEmpty()) {
                List<Supplement> supplements = supplementRepository.findAllById(supplementIds);
                product.setSupplements(supplements);
                product.setSupplementIds(supplementIds);
            }

            if (ingredientIds != null && !ingredientIds.isEmpty()) {
                List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);
                product.setIngredients(ingredients);
                product.setIngredientIds(ingredientIds);
            }

            return productRepository.save(product);
        } else {
            throw new RuntimeException("Category not found with id: " + product.getCategoryId());
        }
    }
    /**
     * Get products by restaurantId.
     * @param restaurantId The restaurant's ID.
     * @return List of products for the restaurant.
     */
    public Page<Product> getProductsByRestaurantId(String restaurantId, int page, int size) {
        // Optional: validate restaurant existence if needed
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findPageByRestaurantId(restaurantId, pageable);
    }


   public List<Product> getProductsByRestaurantId(String restaurantId) {
        return productRepository.findByRestaurantId(restaurantId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Product product, List<String> supplementIds, List<String> ingredientIds) {
        if (supplementIds != null) {
            product.setSupplementIds(supplementIds);
        }

        if (ingredientIds != null) {
            product.setIngredientIds(ingredientIds);
        }

        return productRepository.save(product);
    }

    public Product updateProductImage(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            cloudinaryService.deleteFile(product.getImageUrl());
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Product not found with id: " + id);
        }
    }

    public List<Product> getProductsByCategoryId(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsByRestaurantAndCategory(String restaurantId, String categoryId) {
        return productRepository.findByRestaurantIdAndCategoryId(restaurantId, categoryId);
    }

    // ------------------
    // Méthodes pour récupérer les objets complets

    /*public List<SupplementInfoDTO> getSupplementsByProductId(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getSupplementIds() == null || product.getSupplementIds().isEmpty()) {
            return List.of();
        }

        List<Supplement> supplements = supplementRepository.findAllById(product.getSupplementIds());

        return supplements.stream()
                .map(s -> new SupplementInfoDTO(s.getId(), s.getName(), s.getPrice()))
                .collect(Collectors.toList());
    }*/

    public List<SupplementResponseDTO> getSupplementsByProductId(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Suppose que tu as supplementIds dans Product (List<String>)
        if (product.getSupplementIds() == null || product.getSupplementIds().isEmpty()) {
            return List.of();
        }

        List<Supplement> supplements = supplementRepository.findAllById(product.getSupplementIds());

        return supplements.stream()
                .map(s -> new SupplementResponseDTO(
                        s.getId(),
                        s.getName(),
                        s.getDescription(),
                        s.getPrice()))
                .collect(Collectors.toList());
    }


    public List<IngredientInfoDTO> getIngredientsByProductId(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getIngredientIds() == null || product.getIngredientIds().isEmpty()) {
            return List.of();
        }

        List<Ingredient> ingredients = ingredientRepository.findAllById(product.getIngredientIds());

        return ingredients.stream()
                .map(i -> new IngredientInfoDTO(i.getId(), i.getName()))
                .collect(Collectors.toList());
    }

    public List<IngredientResponseDTO> getIngredientByProductId(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getIngredientIds() == null || product.getIngredientIds().isEmpty()) {
            return List.of();
        }

        List<Ingredient> ingredients = ingredientRepository.findAllById(product.getIngredientIds());

        return ingredients.stream()
                .map(i -> new IngredientResponseDTO(
                        i.getId(),
                        i.getName(),
                        i.getCategoryId(),
                        i.getCreatedby()))
                .collect(Collectors.toList());
    }
}
