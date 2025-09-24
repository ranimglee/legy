package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class FinancierEntity extends UserEntity {

    private String rib;

    public FinancierEntity() {
        super();
        this.setStatus(Status.ACTIVE);
    }

    @Override
    public String getRole() {

        return "FINANCIER";
    }
}




