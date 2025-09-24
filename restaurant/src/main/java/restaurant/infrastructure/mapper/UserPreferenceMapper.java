package restaurant.infrastructure.mapper;

import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.UserPreference;
import restaurant.infrastructure.Document.MongoUserPreference;

import java.util.stream.Collectors;

public class UserPreferenceMapper {

    public static MongoUserPreference toMongo(UserPreference d) {
        if (d == null) return null;
        var m = new MongoUserPreference();
        m.setId(d.getId());
        m.setUserId(d.getUserId());
        m.setCategoryNames(d.getCategoryNames());
        m.setCuisineTypes(
                d.getCuisineTypes()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toList())
        );
        return m;
    }

    public static UserPreference toDomain(MongoUserPreference m) {
        if (m == null) return null;
        var d = new UserPreference();
        d.setId(m.getId());
        d.setUserId(m.getUserId());
        d.setCategoryNames(m.getCategoryNames());
        d.setCuisineTypes(
                m.getCuisineTypes()
                        .stream()
                        .map(MainCuisineType::valueOf)
                        .collect(Collectors.toList())
        );
        return d;
    }
}
