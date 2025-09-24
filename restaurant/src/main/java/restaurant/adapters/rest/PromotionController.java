package restaurant.adapters.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restaurant.application.dto.Promotion.PromotionRequestDTO;
import restaurant.application.dto.Promotion.PromotionResponseDTO;
import restaurant.application.usecase.Promotion.*;
import restaurant.application.dto.Promotion.PromotionAmountDTO;
import restaurant.application.usecase.Promotion.AddPromotionUseCase;
import restaurant.application.usecase.Promotion.GetActivePromotionAmountUseCase;
import restaurant.config.JwtConfig;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;

import java.util.Optional;

import java.io.IOException;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final AddPromotionUseCase addPromotion;
    private final UpdatePromotionUseCase updatePromotion;
    private final UploadPromotionImageUseCase uploadImage;
    private final GetPromotionUseCase getPromotion;
    private final DeletePromotionUseCase deletePromotion;
    private final GetActivePromotionAmountUseCase getActivePromotionAmountUseCase;
    private final RestaurantRepository restaurantRepository;
    private final JwtConfig jwtConfig;


    @PostMapping("/add-promotion")
    public PromotionResponseDTO addPromotion(@Valid @RequestBody PromotionRequestDTO request,
                                             HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String managerId = jwtConfig.extractUserIdFromAccessToken(token);
            System.out.println("Extracted ManagerId from JWT: " + managerId);

            Restaurant restaurant = restaurantRepository.findByCreatedBy(managerId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found for this manager"));

            return addPromotion.execute(request, restaurant.getId());
        } else {
            throw new RuntimeException("JWT token missing or invalid.");
        }
    }


    @GetMapping("/amount")
    public ResponseEntity<PromotionAmountDTO> getPromotionAmount(@RequestParam String productId) {
         Optional<Double> amountOpt = getActivePromotionAmountUseCase.execute(productId);
        return amountOpt
                .map(amount -> ResponseEntity.ok(new PromotionAmountDTO(amount)))
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> update(@PathVariable String id, @RequestBody PromotionRequestDTO request) {
        return ResponseEntity.ok(updatePromotion.execute(id, request));
        }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(uploadImage.execute(id, file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(getPromotion.byId(id));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deletePromotion.execute(id);
        return ResponseEntity.noContent().build();
    }
}

