package restaurant.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.UserPreferenceRequestDTO;
import restaurant.application.dto.UserPreferenceResponseDTO;
import restaurant.application.usecase.UserPreferenceUseCase;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
@Tag(name = "User Preferences", description = "Endpoints for managing a user's restaurant and cuisine preferences")

public class UserPreferenceController {

    private final UserPreferenceUseCase useCase;

    @Operation(
            summary = "Get user preferences",
            description = "Returns the saved restaurant and cuisine preferences for the authenticated user."
    )
    @GetMapping
    public ResponseEntity<UserPreferenceResponseDTO> get(
            @RequestHeader("Authorization") String auth
    ) {
        return ResponseEntity.ok(useCase.get(auth));
    }


    @Operation(
            summary = "Save or update preferences",
            description = "Saves or updates the authenticated user's preferences."
    )
    @PostMapping
    public ResponseEntity<UserPreferenceResponseDTO> save(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody UserPreferenceRequestDTO req
    ) {
        return ResponseEntity.ok(useCase.saveOrUpdate(auth, req));
    }

    @Operation(
            summary = "Delete preferences",
            description = "Deletes all saved preferences for the authenticated user."
    )
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String auth
    ) {
        useCase.delete(auth);
        return ResponseEntity.noContent().build();
    }
}
