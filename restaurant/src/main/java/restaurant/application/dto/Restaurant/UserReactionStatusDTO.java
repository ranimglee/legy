package restaurant.application.dto.Restaurant;


public record UserReactionStatusDTO(String reaction) {
    public static UserReactionStatusDTO of(String reaction) {
        return new UserReactionStatusDTO(reaction);
    }

    public static UserReactionStatusDTO none() {
        return new UserReactionStatusDTO(null);
    }
}
