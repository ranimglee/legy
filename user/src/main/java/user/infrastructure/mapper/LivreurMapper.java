package user.infrastructure.mapper;

import user.domain.model.LivreurEntity;

import user.infrastructure.persistence.entities.MongoLivreurEntity;

public class LivreurMapper {

    public static MongoLivreurEntity toMongo(LivreurEntity domain) throws IllegalAccessException, InstantiationException {
        MongoLivreurEntity mongoEntity = UserMapper.toMongo(domain, MongoLivreurEntity.class);

        mongoEntity.setRib(domain.getRib());
        mongoEntity.setAddress(domain.getAddress());
        mongoEntity.setMatricule(domain.getMatricule());
        mongoEntity.setIsAssigned(domain.getIsAssigned());

        return mongoEntity;
    }

    public static LivreurEntity toDomain(MongoLivreurEntity mongo) throws IllegalAccessException, InstantiationException {
        LivreurEntity domain = new LivreurEntity();
        UserMapper.toDomain(mongo, domain);
        domain.setRib(mongo.getRib());
        domain.setAddress(mongo.getAddress());
        domain.setMatricule(mongo.getMatricule());
        domain.setIsAssigned(mongo.getIsAssigned());

        return domain;
    }



}
