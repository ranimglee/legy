package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UserEntity {


    private String id;

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phoneNumber;
    private Double longitude;
    private Double latitude;

    @Field("status")
    private Status status;

    @Field("refresh_token")
    private String refreshToken;
    private String fcmToken;
    private Instant createdAt;

    public abstract String getRole();



}
