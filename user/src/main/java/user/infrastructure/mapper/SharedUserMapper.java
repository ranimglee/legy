package user.infrastructure.mapper;

import shared.domain.model.LivreurEntity;
import user.infrastructure.persistence.entities.MongoLivreurEntity;

public class SharedUserMapper {

    public static LivreurEntity toSharedDomain(MongoLivreurEntity mongo) {
        LivreurEntity domain = new LivreurEntity();
        domain.setId(mongo.getId());
        domain.setUsername(mongo.getUsername());
        domain.setFirstname(mongo.getFirstname());
        domain.setLastname(mongo.getLastname());
        domain.setEmail(mongo.getEmail());
        domain.setPassword(mongo.getPassword());
        domain.setPhoneNumber(mongo.getPhoneNumber());
        domain.setLongitude(mongo.getLongitude());
        domain.setLatitude(mongo.getLatitude());
        //domain.setStatus(mongo.getStatus());
        domain.setRefreshToken(mongo.getRefreshToken());
        domain.setRib(mongo.getRib());
        domain.setMatricule(mongo.getMatricule());
        return domain;
    }
}
