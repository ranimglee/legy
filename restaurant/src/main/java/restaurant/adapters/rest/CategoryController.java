package restaurant.adapters.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Category.CategoryRequestDTO;
import restaurant.application.dto.Category.CategoryResponseDTO;
import restaurant.application.usecase.Category.*;
import restaurant.config.JwtConfig;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {

    private final AddCategoryUseCase addCategoryUseCase;
    private final GetAllCategoriesUseCase getAllCategoriesUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;

    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final GetTopCategoriesUseCase getTopCategoriesUseCase;
    private final JwtConfig jwtConfig;
    private final SearchCategoriesUseCase searchCategoriesUseCase;




    @PostMapping
    public CategoryResponseDTO addCategory(@Valid @RequestBody CategoryRequestDTO request,
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


        return addCategoryUseCase.execute(request);
    }


    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {
        return getAllCategoriesUseCase.execute();
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable String id) {
        return getCategoryByIdUseCase.execute(id);
    }



    @PutMapping("/{id}")
    public CategoryResponseDTO updateCategory(@PathVariable String id,
                                              @Valid @RequestBody CategoryRequestDTO request) {
        return updateCategoryUseCase.execute(id, request);
    }


    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable String id) {
        deleteCategoryUseCase.execute(id);
    }

    @GetMapping("/top")
    public ResponseEntity<List<CategoryResponseDTO>> getTopCategories(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(getTopCategoriesUseCase.execute(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponseDTO>> search(
            @RequestParam("q") String q,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                searchCategoriesUseCase.execute(q.trim(), page, size));
    }

}
