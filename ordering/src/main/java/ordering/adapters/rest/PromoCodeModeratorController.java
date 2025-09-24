package ordering.adapters.rest;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import ordering.application.dto.promoCode.PromoCodeRequestDTO;
import ordering.application.dto.promoCode.PromoCodeResponseDTO;
import ordering.application.dto.promoCode.PromoCodeUpdateDTO;
import ordering.application.exception.PromoCodeNotFoundException;
import ordering.application.mapper.PromoCodeDtoMapper;
import ordering.domain.model.PromoCode;
import ordering.domain.repository.PromoCodeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/moderateur/promo-codes")
@RequiredArgsConstructor
@Tag(name = "Moderator Promo Codes", description = "Endpoints for moderators to manage promo codes.")

public class PromoCodeModeratorController {

    private final PromoCodeRepository repository;

    @Operation(summary = "Create a new promo code", description = "Creates a unique promo code with discount percentage and usage constraints.")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PromoCodeRequestDTO dto) {
        if (repository.findByCode(dto.code()).isPresent()) {
            return ResponseEntity.status(409).body("Promo code already exists.");
        }

        PromoCode promo = new PromoCode(
                UUID.randomUUID().toString(),
                dto.code(),
                dto.discountValue(),
                dto.startDate(),
                dto.endDate(),
                dto.maxUsage(),
                0
        );

        repository.save(promo);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "List all promo codes", description = "Returns all promo codes created by moderators.")
    @GetMapping
    public ResponseEntity<List<PromoCodeResponseDTO>> getAll() {
        List<PromoCodeResponseDTO> list = repository.findAll().stream()
                .map(PromoCodeDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Delete a promo code", description = "Deletes a promo code by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Partially update a promo code",
            description = "Updates only the provided fields (e.g., discountValue, dates). All unspecified fields remain unchanged."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patch(@PathVariable String id, @RequestBody PromoCodeUpdateDTO dto) {
        PromoCode existing = repository.findById(id)
                .orElseThrow(() -> new PromoCodeNotFoundException("PromoCode not found"));

        PromoCode updated = new PromoCode(
                id,
                dto.code() != null ? dto.code() : existing.getCode(),
                dto.discountValue() != null ? dto.discountValue() : existing.getDiscountValue(),
                dto.startDate() != null ? dto.startDate() : existing.getStartDate(),
                dto.endDate() != null ? dto.endDate() : existing.getEndDate(),
                dto.maxUsage() != null ? dto.maxUsage() : existing.getMaxUsage(),
                existing.getCurrentUsage()
        );

        repository.save(updated);
        return ResponseEntity.ok().build();
    }

}
