package restaurant.application.dto.Restaurant;


/**
 * Summary of a restaurant for the "populaires" list.
 */
public record RestaurantSummary(
         String id,
         String nom,
         String logoUrl,
         String description,
        double averageRating,
        Integer averagePreparingTime

) {}
