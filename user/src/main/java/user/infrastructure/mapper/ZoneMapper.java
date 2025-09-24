package user.infrastructure.mapper;

import user.domain.model.DeliveryZone;
import user.infrastructure.persistence.entities.ZoneDocument;

public class ZoneMapper {

    // Convert ZoneDocument to domain model DeliveryZone
    public static DeliveryZone toDomain(ZoneDocument zoneDocument) {
        if (zoneDocument == null) {
            return null;
        }

        DeliveryZone zone = new DeliveryZone();
        zone.setZoneId(zoneDocument.getZoneId());
        zone.setZoneName(zoneDocument.getZoneName());
        zone.setMaxCapacity(zoneDocument.getMaxCapacity());
        zone.setEnabled(zoneDocument.isEnabled());
        zone.setCoordinates(zoneDocument.getCoordinates());
        zone.setNbrAssignedDrivers(zoneDocument.getNbrAssignedDrivers());
        zone.setNbrRestaurants(zoneDocument.getNbrRestaurants());
        zone.setColor(zoneDocument.getColor());
        zone.setAssignedRestaurants(zoneDocument.getAssignedRestaurants());

        return zone;
    }

    // Convert domain model DeliveryZone to ZoneDocument
    public static ZoneDocument toMongo(DeliveryZone zone) {
        if (zone == null) {
            return null;
        }

        ZoneDocument zoneDocument = new ZoneDocument(
                zone.getZoneId(),
                zone.getZoneName(),
                zone.getMaxCapacity(),
                zone.isEnabled(),
                zone.getCoordinates(),
                zone.getNbrAssignedDrivers(),
                zone.getNbrRestaurants(),
                zone.getColor(),
                zone.getAssignedRestaurants()
        );

        return zoneDocument;
    }
}
