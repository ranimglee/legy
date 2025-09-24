package ordering.application.dto.order;

public record ClientInfoDTO(
        String clientId,
        String firstName,
        String lastName,
        String phone,
        String address,
        Double longitude,
        Double latitude
) {


}
