package shared.dto;

import lombok.NoArgsConstructor;

public record ClientProfileDTO(
        String id,
        String firstname,
        String lastname,
        String phoneNumber,
        String address,
        Double longitude,
        Double latitude
) {

}