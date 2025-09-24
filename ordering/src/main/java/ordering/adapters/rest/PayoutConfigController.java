package ordering.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ordering.application.dto.payout.PayoutConfigEntityResponseDto;
import ordering.application.dto.payout.PayoutConfigRequest;
import ordering.domain.model.PayoutConfigEntity;
import ordering.domain.service.PayoutConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/financier/payout-config")
@RequiredArgsConstructor
@Tag(name = "Payout Configuration Management", description = "APIs for managing payout configurations for financiers")
public class PayoutConfigController {

    private final PayoutConfigService payoutConfigService;

    @Operation(summary = "Get payout config by ID", description = "Retrieve a specific payout configuration by its ID")
    @GetMapping("/get-payout-config-by-id/{id}")
    public ResponseEntity<PayoutConfigEntity> getPayoutConfigById(@PathVariable String id) {
        Optional<PayoutConfigEntity> payoutConfig = payoutConfigService.getPayoutConfigById(id);
        return payoutConfig.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Get all payout configurations", description = "Retrieve all existing payout configurations")
    @GetMapping("/get-all-payout-configurations")
    public Iterable<PayoutConfigEntity> getAllPayoutConfigs() {
        return payoutConfigService.getAllPayoutConfigs();
    }



    @Operation(summary = "Update payout config", description = "Update an existing payout configuration by ID")
    @PutMapping("/update-payout-config/{id}")
    public ResponseEntity<PayoutConfigEntityResponseDto> updatePayoutConfig(
            @PathVariable String id, @RequestBody PayoutConfigRequest updatedConfig) {
        Optional<PayoutConfigEntityResponseDto> updatedEntity = payoutConfigService.updatePayoutConfig(id, updatedConfig);
        return updatedEntity.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete payout config", description = "Delete a payout configuration by ID")
    @DeleteMapping("/delete-payout-config/{id}")
    public ResponseEntity<Void> deletePayoutConfig(@PathVariable String id) {
        boolean isDeleted = payoutConfigService.deletePayoutConfig(id);
        return isDeleted ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/default")
    public PayoutConfigEntity getDefaultPayoutConfig() {
        return payoutConfigService.getPayoutConfigById("default")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}


