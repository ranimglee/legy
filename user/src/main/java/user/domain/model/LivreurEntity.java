package user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class LivreurEntity extends UserEntity{

    private String rib;
    private String matricule;
    private String address;
    private List<Shift> shifts;
    private Boolean isAssigned;
    public LivreurEntity() {
        super();
        this.setStatus(Status.ACTIVE);
    }

    @Override
    public String getRole() {
        return "LIVREUR";
    }

}