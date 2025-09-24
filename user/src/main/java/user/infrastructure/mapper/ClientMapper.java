package user.infrastructure.mapper;

import shared.dto.ClientProfileDTO;
import user.domain.model.ClientEntity;
import user.infrastructure.persistence.entities.MongoClientEntity;

public class ClientMapper {

    public static MongoClientEntity toMongo(ClientEntity domain) throws IllegalAccessException, InstantiationException {
        MongoClientEntity mongo = UserMapper.toMongo(domain, MongoClientEntity.class);
        mongo.setAddress(domain.getAddress());
        mongo.setProvider(domain.getProvider());
        mongo.setGuestSessionId(domain.getGuestSessionId());
        return mongo;
    }

    public static ClientEntity toDomain(MongoClientEntity mongo) throws IllegalAccessException, InstantiationException {
        ClientEntity domain = new ClientEntity();
        UserMapper.toDomain(mongo, domain);
        domain.setAddress(mongo.getAddress());
        domain.setProvider(mongo.getProvider());
        domain.setGuestSessionId(mongo.getGuestSessionId());
        return domain;
    }
    public static ClientProfileDTO toDto(ClientEntity entity) {
        return new ClientProfileDTO(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getPhoneNumber(),
                entity.getAddress(),
                entity.getLongitude(),
                entity.getLatitude()
        );
    }
}
