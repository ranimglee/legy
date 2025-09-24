package restaurant.infrastructure.mapper;

import restaurant.domain.model.Restaurant;
import restaurant.infrastructure.Document.MongoRestaurant;

public class RestaurantMapper {

    public static MongoRestaurant toMongo(Restaurant domain) {
        if (domain == null) return null;

        MongoRestaurant mongo = new MongoRestaurant();
        // BaseAudit fields (createdBy, createdAt…) are handled in BaseAuditDocument

        mongo.setId(domain.getId());
        mongo.setNom(domain.getNom());
        mongo.setAdresse(domain.getAdresse());

        // ← NEW: persist description
        mongo.setDescription(domain.getDescription());

        mongo.setRib(domain.getRib());
        mongo.setIdFisc(domain.getIdFisc());
        mongo.setPickup(domain.isPickup());
        mongo.setLogo(domain.getLogo());
        mongo.setTelephone(domain.getTelephone());
        mongo.setEmail(domain.getEmail());

        mongo.setLongitude(domain.getLongitude());
        mongo.setLatitude(domain.getLatitude());
        mongo.setLocation(new double[]{
                domain.getLongitude(),
                domain.getLatitude()
        });
        mongo.setCreatedBy(domain.getCreatedby());

        mongo.setCommission(domain.getCommission());
        mongo.setRevenueTotalCommission(domain.getRevenueTotalCommission());
        mongo.setNbrCommandesTotal(domain.getNbrCommandesTotal());

        mongo.setAverageRating(domain.getAverageRating());
        mongo.setRatingCount(domain.getRatingCount());

        // ---- cuisine & other fields ----
        mongo.setMainCuisineType(domain.getMainCuisineType());
        mongo.setInternationalCuisine(domain.getInternationalCuisine());
        mongo.setAvailability(domain.getAvailability());

        mongo.setCommission(domain.getCommission());
        mongo.setRevenueTotalCommission(domain.getRevenueTotalCommission());
        mongo.setNbrCommandesTotal(domain.getNbrCommandesTotal());
        mongo.setFollowerCount(domain.getFollowerCount());
        mongo.setHoraires(domain.getHoraires());
        mongo.setIsAssigned(domain.getIsAssigned());
        mongo.setAssignedZoneId(domain.getAssignedZoneId());
        mongo.setRestaurantStatus(domain.getRestaurantStatus());
        mongo.setManagerId(domain.getManagerId());

        return mongo;
    }

    public static Restaurant toDomain(MongoRestaurant mongo) {
        if (mongo == null) return null;

        Restaurant domain = new Restaurant();
        // BaseAudit fields (createdBy, createdAt…) are set via BaseAuditDomain

        domain.setId(mongo.getId());
        domain.setNom(mongo.getNom());
        domain.setAdresse(mongo.getAdresse());

        // ← NEW: load description
        domain.setDescription(mongo.getDescription());

        domain.setRib(mongo.getRib());
        domain.setIdFisc(mongo.getIdFisc());
        domain.setPickup(mongo.isPickup());
        domain.setLogo(mongo.getLogo());
        domain.setTelephone(mongo.getTelephone());
        domain.setEmail(mongo.getEmail());

        domain.setLongitude(mongo.getLongitude());
        domain.setLatitude(mongo.getLatitude());

        domain.setAverageRating(mongo.getAverageRating());
        domain.setRatingCount(mongo.getRatingCount());
        domain.setCreatedby(mongo.getCreatedby());

        // ---- cuisine & other fields ----
        domain.setMainCuisineType(mongo.getMainCuisineType());
        // setter will null‐out internationalCuisine if main≠INTERNATIONALE
        domain.setInternationalCuisine(mongo.getInternationalCuisine());
        domain.setAvailability(mongo.getAvailability());

        domain.setCommission(mongo.getCommission());
        domain.setRevenueTotalCommission(mongo.getRevenueTotalCommission());
        domain.setNbrCommandesTotal(mongo.getNbrCommandesTotal());
        domain.setFollowerCount(mongo.getFollowerCount());
        domain.setHoraires(mongo.getHoraires());
        domain.setIsAssigned(mongo.getIsAssigned());
        domain.setAssignedZoneId(mongo.getAssignedZoneId());
        domain.setRestaurantStatus(mongo.getRestaurantStatus());
        domain.setManagerId(mongo.getManagerId());

        return domain;
    }
}
