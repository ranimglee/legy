package shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class LivreurEntity extends UserEntity {

    private String rib;
    private String matricule;

    public LivreurEntity() {
        super();
        this.setStatus(Status.ACTIVE);
    }

    @Override
    public String getRole() {
        return "LIVREUR";
    }

}