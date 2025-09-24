package restaurant.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import restaurant.domain.model.MainCuisineType;

import java.util.List;

public record UserPreferenceRequestDTO(
        @NotNull List<@NotBlank String> categoryNames,
        @NotNull List<MainCuisineType> cuisineTypes
) {
}