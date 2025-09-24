package user.application.dto.out;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ModeratorProfileResponse {
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String rib;
    private Instant createdAt;

}
