package user.infrastructure.mapper;

import user.domain.model.ModerateurEntity;
import user.infrastructure.persistence.entities.MongoModerateurEntity;

public class ModerateurMapper {

    public static MongoModerateurEntity toMongo(ModerateurEntity domain) throws IllegalAccessException, InstantiationException {
        MongoModerateurEntity mongo = UserMapper.toMongo(domain, MongoModerateurEntity.class);

        // ✅ Explicitly set Moderateur-specific fields
        mongo.setRib(domain.getRib());

        return mongo;
    }

    public static ModerateurEntity toDomain(MongoModerateurEntity mongo) throws IllegalAccessException, InstantiationException {
        ModerateurEntity domain = new ModerateurEntity();

        // Map shared fields
        UserMapper.toDomain(mongo, domain);

        // ✅ Map Moderateur-specific fields
        domain.setRib(mongo.getRib());


        return domain;
    }
}
