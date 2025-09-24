package restaurant.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserPreference extends BaseAuditDomain {
    private String id;
    private String userId;
    private List<String> categoryNames;
    private List<MainCuisineType> cuisineTypes;
}
