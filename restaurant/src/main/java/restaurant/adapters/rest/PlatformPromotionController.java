package restaurant.adapters.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.PromotionPlatform.CreatePromotionDto;
import restaurant.application.dto.PromotionPlatform.PromotionResponseDto;
import restaurant.application.usecase.PlatformPromotion.CreatePlatformPromotionUseCase;
import restaurant.domain.model.PromotionPlatform;
import restaurant.domain.service.PlatformPromotionService;
import shared.enums.PromotionType;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/moderateur/platform-promotions")
@RequiredArgsConstructor
public class PlatformPromotionController {

    private final CreatePlatformPromotionUseCase createUseCase;
    private final PlatformPromotionService service;

    @PostMapping("/create-new-promo")
    public ResponseEntity<PromotionResponseDto> createPromotion(@RequestBody CreatePromotionDto dto) {
        PromotionResponseDto response = createUseCase.execute(dto);
        return ResponseEntity.ok(response);
    }

    // ✅ switch activation status
    @PatchMapping("/switch-promotion-status/{id}")
    public ResponseEntity<PromotionResponseDto> toggleActive(@PathVariable String id) {
        return service.toggleActiveStatus(id)
                .map(promo -> ResponseEntity.ok(PromotionResponseDto.fromDomain(promo)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Delete
    @DeleteMapping("/delete-promotion/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable String id) {
        boolean deleted = service.deletePromotion(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @GetMapping("/get-all-promotions")
    public ResponseEntity<Page<PromotionResponseDto>> getPaginatedPromotions(
            @RequestParam(required = false) PromotionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PromotionResponseDto> result = service.getAllPromotions(type, pageable)
                .map(PromotionResponseDto::fromDomain);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/expired")
    public ResponseEntity<List<PromotionResponseDto>> getExpiredPromotions() {
        List<PromotionResponseDto> list = service.getExpiredPromotions().stream()
                .map(PromotionResponseDto::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
    @PutMapping("/update-promotion/{id}")
    public ResponseEntity<PromotionResponseDto> updatePromotion(
            @PathVariable String id,
            @RequestBody CreatePromotionDto dto
    ) {
        PromotionPlatform updatedPromo = new PromotionPlatform(
                id,
                dto.title(),
                dto.description(),
                dto.type(),
                dto.discountValue(),
                dto.startDate(),
                dto.endDate(),
                dto.active()

        );

        return service.updatePromotion(id, updatedPromo)
                .map(p -> ResponseEntity.ok(PromotionResponseDto.fromDomain(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}