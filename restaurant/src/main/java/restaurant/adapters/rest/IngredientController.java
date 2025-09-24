package restaurant.adapters.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Ingredient.IngredientRequestDTO;
import restaurant.application.dto.Ingredient.IngredientResponseDTO;
import restaurant.application.usecase.Ingredient.*;
import restaurant.config.JwtConfig;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final AddIngredientUseCase addIngredientUseCase;
    private final GetAllIngredientsUseCase getAllIngredientsUseCase;
    private final GetIngredientByIdUseCase getIngredientByIdUseCase;
    private final UpdateIngredientUseCase updateIngredientUseCase;
    private final DeleteIngredientUseCase deleteIngredientUseCase;
    private final GetIngredientsByCategoryUseCase getIngredientsByCategoryUseCase;
    private final JwtConfig jwtConfig;
    private final GetIngredientsByProductIdUseCase getIngredientsByProductIdUseCase;
    private final SearchIngredientsUseCase searchIngredients;


    @Autowired
    public IngredientController(AddIngredientUseCase addIngredientUseCase,
                                GetAllIngredientsUseCase getAllIngredientsUseCase,
                                GetIngredientByIdUseCase getIngredientByIdUseCase,
                                UpdateIngredientUseCase updateIngredientUseCase,
                                DeleteIngredientUseCase deleteIngredientUseCase,
                                GetIngredientsByCategoryUseCase getIngredientsByCategoryUseCase,
                                JwtConfig jwtConfig, GetIngredientsByProductIdUseCase getIngredientsByProductIdUseCase, SearchIngredientsUseCase searchIngredients
    ) {
        this.addIngredientUseCase = addIngredientUseCase;
        this.getAllIngredientsUseCase = getAllIngredientsUseCase;
        this.getIngredientByIdUseCase = getIngredientByIdUseCase;
        this.updateIngredientUseCase = updateIngredientUseCase;
        this.deleteIngredientUseCase = deleteIngredientUseCase;
        this.getIngredientsByCategoryUseCase = getIngredientsByCategoryUseCase;
        this.jwtConfig = jwtConfig;
        this.getIngredientsByProductIdUseCase = getIngredientsByProductIdUseCase;
        this.searchIngredients = searchIngredients;
    }

    @PostMapping
    public IngredientResponseDTO addIngredient(@Valid @RequestBody IngredientRequestDTO request,
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
        return addIngredientUseCase.execute(request);
    }

    @GetMapping
    public List<IngredientResponseDTO> getAllIngredients() {
        return getAllIngredientsUseCase.execute();
    }

    @GetMapping("/{id}")
    public IngredientResponseDTO getIngredientById(@PathVariable String id) {
        return getIngredientByIdUseCase.execute(id);
    }

    @PutMapping("/{id}")
    public IngredientResponseDTO updateIngredient(@PathVariable String id,
                                                  @Valid @RequestBody IngredientRequestDTO request) {
        return updateIngredientUseCase.execute(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteIngredient(@PathVariable String id) {
        deleteIngredientUseCase.execute(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<IngredientResponseDTO> getIngredientsByCategory(@PathVariable String categoryId) {
        return getIngredientsByCategoryUseCase.execute(categoryId);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<IngredientResponseDTO>> getByProductId(@PathVariable String productId) {
        List<IngredientResponseDTO> ingredients = getIngredientsByProductIdUseCase.execute(productId);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/search")
    public ResponseEntity<List<IngredientResponseDTO>> search(
            @RequestParam("q")    String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (q == null || q.isBlank())
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(searchIngredients.execute(q.trim(), page, size));
    }

}
