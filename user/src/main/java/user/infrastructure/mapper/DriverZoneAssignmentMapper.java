package user.infrastructure.mapper;

import user.domain.model.DriverZoneAssignment;
import user.infrastructure.persistence.entities.MongoDriverZoneAssignment;

public class DriverZoneAssignmentMapper {

    public static DriverZoneAssignment toDomain(MongoDriverZoneAssignment mongo) {
        if (mongo == null) {
            return null;
        }

        DriverZoneAssignment domain = new DriverZoneAssignment();
        domain.setId(mongo.getId());
        domain.setLivreurId(mongo.getLivreurId()); // now using just ID
        domain.setZoneId(mongo.getZoneId());
        domain.setAssigned(mongo.isAssigned());
        domain.setLatitude(mongo.getLatitude());
        domain.setLongitude(mongo.getLongitude());

        return domain;
    }

    public static MongoDriverZoneAssignment toMongo(DriverZoneAssignment domain) {
        if (domain == null) {
            return null;
        }

        MongoDriverZoneAssignment mongo = new MongoDriverZoneAssignment();
        mongo.setId(domain.getId());
        mongo.setLivreurId(domain.getLivreurId()); // simple ID mapping
        mongo.setZoneId(domain.getZoneId());
        mongo.setAssigned(domain.isAssigned());
        mongo.setLatitude(domain.getLatitude());
        mongo.setLongitude(domain.getLongitude());

        return mongo;
    }
}
