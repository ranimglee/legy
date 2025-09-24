package restaurant.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restaurant.application.dto.Ingredient.IngredientInfoDTO;
import restaurant.application.dto.Product.*;
import restaurant.application.dto.Supplement.SupplementInfoDTO;
import restaurant.application.mapper.ProductResponseMapper;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.application.usecase.Product.*;
import restaurant.config.JwtConfig;
import restaurant.domain.model.Product;
import restaurant.domain.service.ProductDomainService;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import shared.dto.PagedResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final AddProductUseCase addProductUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetProductsByCategoryUseCase getProductsByCategoryUseCase;
    private final GetPopularProductsByCuisineUseCase getProductsUseCase;
    private final GetProductRatingUseCase getProductRatingUseCase;


    private final UploadProductImageUseCase uploadProductImageUseCase;
    private final JwtConfig jwtConfig;
    private final ProductDomainService productDomainService;



    @PostMapping
    public ProductResponseDTO addProduct(@Valid  @RequestBody ProductRequestDTO request,
                                         HttpServletRequest httpRequest) {

        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);  // remove "Bearer " prefix
            String userId = jwtConfig.extractUserIdFromAccessToken(token);
            request.setCreatedby(userId);
            System.out.println("Extracted UserId from JWT: " + userId);
        } else {
            throw new RuntimeException("JWT token missing or invalid.");
        }
        return addProductUseCase.execute(request);
    }

    @GetMapping("/get-all-products")
    public List<ProductResponseDTO> getAllProducts() {
        return getAllProductsUseCase.execute();
    }

    @GetMapping("/get-product-by-id/{id}")
    public ProductResponseDTO getProductById(@PathVariable String id) {
        return getProductByIdUseCase.execute(id);
    }

    @PatchMapping("/update-product-by/{id}")
    public ProductResponseDTO updateProduct(@PathVariable String id,
                                             @RequestBody ProductRequestDTO request) {
        return updateProductUseCase.execute(id, request);
    }

    @PostMapping(
            value = "/upload-product-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE // change to JSON
    )
    public ResponseEntity<ImageUploadResponse> uploadProductImage(@RequestParam("image") MultipartFile image) throws IOException {
        ImageUploadResponse response = uploadProductImageUseCase.execute(image);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        deleteProductUseCase.execute(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDTO> getProductsByCategory(@PathVariable String categoryId) {
        return getProductsByCategoryUseCase.execute(categoryId);
    }

    /**
     * Get all products for a specific restaurant by restaurantId.
     *
     * @param restaurantId The restaurant's ID.
     * @return A list of products for the restaurant.
     */

   @GetMapping("/get-products-by-restaurant/{restaurantId}")
    public ResponseEntity<PagedResponse<ProductResponseDTO>> getProductsByRestaurant(
            @PathVariable String restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> productPage = productDomainService.getProductsByRestaurantId(restaurantId, page, size);

        List<ProductResponseDTO> dtoList = productPage.getContent().stream()
                .map(ProductResponseMapper::toDTO)
                .collect(Collectors.toList());

        PagedResponse<ProductResponseDTO> response = new PagedResponse<>(
                dtoList,
                productPage.getNumber(),
                productPage.getTotalPages(),
                productPage.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/popular")
    public ProductListDTO popularProductsByCuisine(
            @RequestParam MainCuisineType main,
            @RequestParam(required = false) InternationalCuisine sub,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return getProductsUseCase.execute(main, sub, page, size);
    }


    @GetMapping("/{id}/rating")
    public ProductRatingDTO getRating(@PathVariable("id") String productId) {
        return getProductRatingUseCase.execute(productId);
    }
    @Operation(summary = "Get supplements of a product", description = "Returns the list of supplements associated with a given product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of supplements found"),
            @ApiResponse(responseCode = "204", description = "No supplements found for this product"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping("/{id}/supplements")
    public ResponseEntity<List<SupplementInfoDTO>> getProductSupplements(@PathVariable String id) {
        List<SupplementResponseDTO> supplementResponses = productDomainService.getSupplementsByProductId(id);
        if (supplementResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<SupplementInfoDTO> supplements = supplementResponses.stream()
                .map(s -> new SupplementInfoDTO(s.getId(), s.getName(), s.getPrice()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(supplements);
    }

    @Operation(summary = "Get ingredients of a product", description = "Returns the list of ingredients associated with a given product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of ingredients found"),
            @ApiResponse(responseCode = "204", description = "No ingredients found for this product"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/ingredients")
    public ResponseEntity<List<IngredientInfoDTO>> getProductIngredients(@PathVariable String id) {
        List<IngredientInfoDTO> ingredients = productDomainService.getIngredientsByProductId(id);
        if (ingredients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ingredients);
    }

}


