package user.infrastructure.mapper;

import user.domain.model.VerificationToken;
import user.infrastructure.persistence.entities.MongoVerificationToken;

public class VerificationTokenMapper {

    public static MongoVerificationToken toMongo(VerificationToken domain) {
        MongoVerificationToken mongo = new MongoVerificationToken();
        mongo.setId(domain.getId());
        mongo.setToken(domain.getToken());
        mongo.setUserId(domain.getUserId());
        mongo.setExpiresAt(domain.getExpiresAt());
        return mongo;
    }

    public static VerificationToken toDomain(MongoVerificationToken mongo) {
        VerificationToken domain = new VerificationToken();
        domain.setId(mongo.getId());
        domain.setToken(mongo.getToken());
        domain.setUserId(mongo.getUserId());
        domain.setExpiresAt(mongo.getExpiresAt());
        return domain;
    }
}
