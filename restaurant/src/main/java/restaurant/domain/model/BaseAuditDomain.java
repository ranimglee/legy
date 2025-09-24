package restaurant.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseAuditDomain {


    private Instant createdAt;
    private Instant updatedAt;

}
