package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class ClientEntity extends UserEntity{
    private String address;
    private AuthProvider provider = AuthProvider.LOCAL;
    private String guestSessionId;

    public ClientEntity() {
        super();
        this.setStatus(Status.PENDING);
    }

    @Override
    public String getRole() {
        return "CLIENT";
    }
}