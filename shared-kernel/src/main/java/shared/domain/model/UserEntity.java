package shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import shared.domain.model.Status;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UserEntity {

    private String id = UUID.randomUUID().toString();

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phoneNumber;
    private Double longitude;
    private Double latitude;

    private Status status;

    private String refreshToken;

    public abstract String getRole();

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + firstname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", lastname='" + lastname + '\'' +
                ", lastname='" + status + '\'' +

                '}';
    }
}
