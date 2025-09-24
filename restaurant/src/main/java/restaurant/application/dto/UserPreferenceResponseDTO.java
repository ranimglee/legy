package restaurant.application.dto;

import restaurant.domain.model.MainCuisineType;

import java.util.List;


public record UserPreferenceResponseDTO(
        List<String> categoryNames,
        List<MainCuisineType> cuisineTypes
) {
}