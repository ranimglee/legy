package user.infrastructure.mapper;

import user.domain.model.*;
import user.infrastructure.persistence.entities.*;

public class UserMapper {

    public static <T extends UserEntity, R extends MongoUserEntity> R toMongo(T domainUser, Class<R> mongoEntityClass) {
        try {
            // Ensure that we're creating an instance of a concrete subclass
            R mongoUser = mongoEntityClass.getDeclaredConstructor().newInstance();
            mongoUser.setId(domainUser.getId());
            mongoUser.setUsername(domainUser.getUsername());
            mongoUser.setFirstname(domainUser.getFirstname());
            mongoUser.setLastname(domainUser.getLastname());

            mongoUser.setEmail(domainUser.getEmail());
            mongoUser.setPassword(domainUser.getPassword());
            mongoUser.setPhoneNumber(domainUser.getPhoneNumber());
            mongoUser.setLongitude(domainUser.getLongitude());
            mongoUser.setLatitude(domainUser.getLatitude());
            mongoUser.setStatus(domainUser.getStatus());
            mongoUser.setRefreshToken(domainUser.getRefreshToken());
            mongoUser.setFcmToken(domainUser.getFcmToken());
            mongoUser.setCreatedAt(domainUser.getCreatedAt());
            return mongoUser;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error creating MongoUser instance", e);
        }
    }

    // Mapping from MongoDB entity to domain model
    public static UserEntity toDomain(MongoUserEntity mongo) throws IllegalAccessException, InstantiationException {
        if (mongo instanceof MongoClientEntity mongoClient) {
            return toDomain(mongoClient, new ClientEntity());
        } else if (mongo instanceof MongoLivreurEntity mongoLivreur) {
            return toDomain(mongoLivreur, new LivreurEntity());
        } else if (mongo instanceof MongoRestaurantManagerEntity mongoManager) {
            return toDomain(mongoManager, new RestaurantManagerEntity());
        } else if (mongo instanceof MongoModerateurEntity mongoMod) {
            return toDomain(mongoMod, new ModerateurEntity());
        } else {
            throw new IllegalArgumentException("Unknown MongoUserEntity type: " + mongo.getClass().getSimpleName());
        }
    }

    public static <T extends MongoUserEntity, R extends UserEntity> R toDomain(T mongoEntity, R domainEntity) {
        domainEntity.setId(mongoEntity.getId());
        domainEntity.setUsername(mongoEntity.getUsername());
        domainEntity.setFirstname(mongoEntity.getFirstname());
        domainEntity.setLastname(mongoEntity.getLastname());
        domainEntity.setEmail(mongoEntity.getEmail());
        domainEntity.setPassword(mongoEntity.getPassword());
        domainEntity.setPhoneNumber(mongoEntity.getPhoneNumber());
        domainEntity.setLongitude(mongoEntity.getLongitude());
        domainEntity.setLatitude(mongoEntity.getLatitude());
        domainEntity.setStatus(mongoEntity.getStatus());
        domainEntity.setRefreshToken(mongoEntity.getRefreshToken());
        domainEntity.setFcmToken(mongoEntity.getFcmToken());
        domainEntity.setCreatedAt(mongoEntity.getCreatedAt());
        return domainEntity;
    }
}
