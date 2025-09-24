package restaurant.adapters.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Supplement.SupplementRequestDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.application.usecase.Supplement.*;
import restaurant.config.JwtConfig;

import java.util.List;

@RestController
@RequestMapping("/supplements")
public class SupplementController {

    private final AddSupplementUseCase addSupplementUseCase;
    private final GetAllSupplementsUseCase getAllSupplementsUseCase;
    private final GetSupplementByIdUseCase getSupplementByIdUseCase;
    private final UpdateSupplementUseCase updateSupplementUseCase;
    private final DeleteSupplementUseCase deleteSupplementUseCase;
    private final JwtConfig jwtConfig;
    private final SearchSupplementsUseCase searchSupplementsUseCase;

    @Autowired
    public SupplementController(AddSupplementUseCase addSupplementUseCase,
                                GetAllSupplementsUseCase getAllSupplementsUseCase,
                                GetSupplementByIdUseCase getSupplementByIdUseCase,
                                UpdateSupplementUseCase updateSupplementUseCase,
                                DeleteSupplementUseCase deleteSupplementUseCase,
                                JwtConfig jwtConfig, SearchSupplementsUseCase searchSupplementsUseCase) {
        this.addSupplementUseCase = addSupplementUseCase;
        this.getAllSupplementsUseCase = getAllSupplementsUseCase;
        this.getSupplementByIdUseCase = getSupplementByIdUseCase;
        this.updateSupplementUseCase = updateSupplementUseCase;
        this.deleteSupplementUseCase = deleteSupplementUseCase;
        this.jwtConfig = jwtConfig;
        this.searchSupplementsUseCase = searchSupplementsUseCase;
    }
    @PostMapping
    public SupplementResponseDTO addSupplement(@Valid  @RequestBody SupplementRequestDTO request,
                                               HttpServletRequest httpRequest) {

        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userId = jwtConfig.extractUserIdFromAccessToken(token);
            request.setCreatedby(userId);
            System.out.println("Extracted UserId from JWT: " + userId);
        } else {
            throw new RuntimeException("JWT token missing or invalid.");
        }
        return addSupplementUseCase.execute(request);
    }

    @GetMapping
    public List<SupplementResponseDTO> getAllSupplements() {
        return getAllSupplementsUseCase.execute();
    }

    @GetMapping("/{id}")
    public SupplementResponseDTO getSupplementById(@PathVariable String id) {
        return getSupplementByIdUseCase.execute(id);
    }

    @PutMapping("/{id}")
    public SupplementResponseDTO updateSupplement(@PathVariable String id,
                                                  @Valid @RequestBody SupplementRequestDTO request) {
        return updateSupplementUseCase.execute(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteSupplement(@PathVariable String id) {
        deleteSupplementUseCase.execute(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplementResponseDTO>> searchSupplements(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                searchSupplementsUseCase.execute(query.trim(), page, size)
        );
    }
}
