package user.application.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinancialProfileResponse {
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String rib;

}
