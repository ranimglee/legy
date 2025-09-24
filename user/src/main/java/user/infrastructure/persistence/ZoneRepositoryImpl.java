package user.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import user.domain.model.DeliveryZone;
import user.domain.repository.DeliveryZoneRepository;
import user.infrastructure.mapper.ZoneMapper;
import user.infrastructure.persistence.entities.ZoneDocument;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository

public class ZoneRepositoryImpl implements DeliveryZoneRepository {

    private MongoZoneRepository mongoZoneRepository;


    @Override
    public DeliveryZone save(DeliveryZone zone) {
        // Map the domain model (DeliveryZone) to the Mongo document (ZoneDocument)
        ZoneDocument zoneDocument = ZoneMapper.toMongo(zone);

        // Save the document in MongoDB and return the saved DeliveryZone
        ZoneDocument savedZoneDocument = mongoZoneRepository.save(zoneDocument);
        return ZoneMapper.toDomain(savedZoneDocument); // Map back to domain model
    }

    @Override
    public boolean existsById(String zoneId) {
        // Check if a ZoneDocument with the given zoneId exists in the MongoDB repository
        return mongoZoneRepository.existsById(zoneId);
    }

    @Override
    public void deleteById(String zoneId) {
        // Delete the ZoneDocument by zoneId from MongoDB
        mongoZoneRepository.deleteById(zoneId);
    }

    @Override
    public Optional<DeliveryZone> findById(String zoneId) {
        // Fetch the ZoneDocument from the repository
        Optional<ZoneDocument> zoneDocument = mongoZoneRepository.findById(zoneId);

        // Convert the ZoneDocument to a DeliveryZone if it exists
        return zoneDocument.map(ZoneMapper::toDomain); // Map the ZoneDocument to DeliveryZone
    }

    @Override
    public List<DeliveryZone> findAll() {
        List<ZoneDocument> documents = mongoZoneRepository.findAll();
        return documents.stream()
                .map(ZoneMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<DeliveryZone> findByAssignedRestaurantId(String restaurantId) {
        return mongoZoneRepository.findByAssignedRestaurantId(restaurantId)
                .map(ZoneMapper::toDomain);
    }





}
