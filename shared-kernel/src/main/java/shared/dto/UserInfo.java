package shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String userId;
    private String username;
    private String firstname;
    private String lastname;
    private String email;


    public UserInfo(String id, String email) {
        this.userId = id;
        this.email=email;
    }
}