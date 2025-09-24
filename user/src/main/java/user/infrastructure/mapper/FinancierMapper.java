package user.infrastructure.mapper;

import user.domain.model.FinancierEntity;
import user.infrastructure.persistence.entities.MongoFinancierEntity;

public class FinancierMapper {

    public static MongoFinancierEntity toMongo(FinancierEntity domain) throws IllegalAccessException, InstantiationException {
        MongoFinancierEntity mongoEntity = UserMapper.toMongo(domain, MongoFinancierEntity.class);

        mongoEntity.setRib(domain.getRib());

        return mongoEntity;
    }

    public static FinancierEntity toDomain(MongoFinancierEntity mongo) throws IllegalAccessException, InstantiationException {
        FinancierEntity domain = new FinancierEntity();

        UserMapper.toDomain(mongo, domain);
        domain.setRib(mongo.getRib());
        return domain;
    }
}
