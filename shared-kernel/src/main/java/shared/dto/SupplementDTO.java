package shared.dto;

import lombok.Builder;

@Builder
public record SupplementDTO(
        String id,
        String name,
        double price
) {}
