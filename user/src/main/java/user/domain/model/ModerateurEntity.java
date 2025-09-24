package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ModerateurEntity extends UserEntity{
    private String rib;

    public ModerateurEntity() {
        super();
        this.setStatus(Status.ACTIVE);
    }
    @Override
    public String getRole() {
        return "MODERATEUR";
    }

}