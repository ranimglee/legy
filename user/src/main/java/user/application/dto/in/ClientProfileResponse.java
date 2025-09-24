package user.application.dto.in;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientProfileResponse {
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String address;
    private Double longitude;
    private Double latitude;
}
